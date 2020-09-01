import { useRouter, withRouter } from 'next/router'
import React, { useState, useEffect, useCallback } from 'react'
import { Typography } from '@material-ui/core'
import Head from 'next/head'
import Cookie from 'js-cookie'
import { Editor, EditorState, convertFromRaw, convertToRaw, RichUtils, getDefaultKeyBinding, KeyBindingUtil, Modifier } from 'draft-js'
import { debounce, sortedUniq } from 'lodash'

//used because EditorState.createFromEmpty() was producing errors.
//just an empty content state
const emptyContentState = convertFromRaw({
    entityMap: {},
    blocks: [
      {
        text: '',
        key: 'foo',
        type: 'unstyled',
        entityRanges: [],
      },
    ],
  });

//used for writing notes
//todo visual saving indicator
function Note() {
    
    const { hasCommandModifier } = KeyBindingUtil

    const router = useRouter()
    const { title } = router.query
    const jwt = Cookie.get('jwt')
    const [ editorState, setEditorState ] = useState(() => EditorState.createWithContent(emptyContentState))
    const [ textShortcuts, setTextShortcuts] = useState([])
    const [ styleShortcuts, setStyleShortcuts ] = useState([])
    const [ styleMap, setStyleMap ] = useState({})

    if(!jwt) {
        console.log('Unauthenticated')
    }

    useEffect(() => {
        if(title) {
            getFromServer()
        }
    }, [ title ])

    //saves once user stops typing for one second. todo probly need to save less often
    const debounceSave = useCallback(debounce(async (newEditorState) => {
        console.log('saving...')
        if(!jwt || !title) {
            console.log("can't save!")
            console.log('jwt: ' + jwt + '; title: ' + title)
            return;
        }
        const requestOptions = {
            method: 'PATCH',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt},
            body: JSON.stringify({
                title: title,
                body: convertToRaw(newEditorState.getCurrentContent())
            })
        }
        const response = await fetch('http://localhost:8080/api/v1/notes/save', requestOptions)
    }, 1500), [ jwt, title])

    const insertTextAtCursor = (text) => {
        let contentState = editorState.getCurrentContent()
        let targetRange = editorState.getSelection()
        let newContentState = Modifier.insertText(
            contentState,
            targetRange,
            text
        )
        let newEditorState = EditorState.push(
            editorState,
            newContentState
        )
                
        //changing the position of the cursor to be at the end of the shortcutted text
        const nextOffSet = newEditorState.getSelection().getFocusOffset()
        const newSelection = newEditorState.getSelection().merge({
            focusOffset: nextOffSet,
            anchorOffset: nextOffSet
        })
        onChange(EditorState.acceptSelection(newEditorState, newSelection))
    }

    // used for keyboard shortcuts
    const handleKeyCommand = (command) => {
        //special key binds that are assigned from the start
        //todo make it so that users cannot make keybinds that start with nottte-
        if(command === 'nottte-save') {
            debounceSave(editorState) //todo make this not a debounce
            return 'handled'
        }
        else if(command === 'nottte-tab') {
            insertTextAtCursor('\t')
            return 'handled'
        } 

        //text shortcuts
        for(var i = 0; i < textShortcuts.length; i++) {
            let shortcut = textShortcuts[i]
            if(shortcut.name == command) {
                let contentState = editorState.getCurrentContent()
                let targetRange = editorState.getSelection()
                let newContentState = Modifier.insertText(
                    contentState,
                    targetRange,
                    shortcut.text
                )
                let newEditorState = EditorState.push(
                    editorState,
                    newContentState
                )
                
                //changing the position of the cursor to be at the end of the shortcutted text
                const nextOffSet = newEditorState.getSelection().getFocusOffset()
                const newSelection = newEditorState.getSelection().merge({
                    focusOffset: nextOffSet,
                    anchorOffset: nextOffSet
                })
                onChange(EditorState.acceptSelection(newEditorState, newSelection))
                return 'handled'
            }
        }

        //style shortcuts
        for(var i = 0; i < styleShortcuts.length; i++) {
            let shortcut = styleShortcuts[i]
            if(shortcut.name == command) {
                //calling on change because it waits for the new editor state to update first
                //else it would save the old editor state
                setEditorState(RichUtils.toggleInlineStyle(editorState, shortcut.name))
                return 'handled'
            }
        }

        const newState = RichUtils.handleKeyCommand(editorState, command)
        if (newState) {
          onChange(newState)
          return 'handled'
        }
        return 'not-handled'
    }

    const onChange = async (newEditorState) => {
        await setEditorState(newEditorState)
        debounceSave(newEditorState)
    }
    
    const getFromServer = async () => {
        if(!jwt) return; //todo prompt sign in or un-auth'd note

        const requestOptions = {
            method: 'GET',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt}
        }

        //getting style shortcuts
        //ideally, you'd want to get the editorState first so that it could load faster for the user
        //but without a stylemap, provided by the style shortcuts, the editor state gets loaded before stylemaps are applied,
        //and it isn't updated until a new style shortcut is applied :(
        const styleShortcutResponse = await fetch('http://localhost:8080/api/v1/users/principal/preferences/shortcuts/style', requestOptions)
        const styleShortcutText = await styleShortcutResponse.text()
        
        if(styleShortcutResponse.status === 401) return;
        if(styleShortcutResponse.status === 403) return;
        if(styleShortcutResponse.status === 404) return;
        
        const parsedStyleShortcuts = JSON.parse(styleShortcutText)
        await setStyleShortcuts(parsedStyleShortcuts)

        let newStyleMap = {}
        for(var i = 0; i < parsedStyleShortcuts.length; i++) {
            let styleShortcut = parsedStyleShortcuts[i]
            let name = styleShortcut.name, attribute = styleShortcut.attribute, value = styleShortcut.value

            //updating styleMap object with new info
            newStyleMap = {
                ...newStyleMap,
                [name]: {
                    [attribute]: value
                }
            }
        }
        await setStyleMap(newStyleMap)

        

        //getting editor state

        const editorResponse = await fetch('http://localhost:8080/api/v1/notes/note/' + encodeURI(title) + '/raw', requestOptions)
        const editorText = await editorResponse.text()
        console.log(editorResponse.status)

        //todo show error for 404s and 401s
        if(editorResponse.status === 401) return
        if(editorResponse.status === 404) return
        if(editorResponse.status === 403) return
        if(editorResponse.status === 500) return

        if(editorResponse === '') {
            setEditorState(EditorState.createEmpty())
        } else {
            const parsedText = JSON.parse(editorText)
            const textFromRaw = convertFromRaw(parsedText)
            const textEditorState = EditorState.createWithContent(textFromRaw)
            setEditorState(textEditorState)
        }

        //getting shortcuts

        const textShortcutResponse = await fetch('http://localhost:8080/api/v1/users/principal/preferences/shortcuts', requestOptions)
        const textShortcutText = await textShortcutResponse.text()

        //todo
        if(textShortcutResponse.status === 401) return;
        if(textShortcutResponse.status === 404) return;
        if(textShortcutResponse.status === 403) return;
        setTextShortcuts(JSON.parse(textShortcutText))

        
    }
    
    //e is a SyntheticKeyboardEvent. imagine being weakly typed
    const keyBindingFn = (e) => {

        if(e.key === 'Tab') {
            return 'nottte-tab'
        }

        //used for clarity and non-shortcut efficiency
        if(!hasCommandModifier(e)) {
            return getDefaultKeyBinding(e);
        }


        //todo prevent users from making CTRL + S shortcuts
        //saving note with shortcut
        if(e.key == 's') {
            e.preventDefault()
            return 'nottte-save'
        }

        for(var i = 0; i < textShortcuts.length; i++) {
            let shortcut = textShortcuts[i]
            if(e.key == shortcut.key) {
                console.log('ran: ' + shortcut.name)
                e.preventDefault()
                return shortcut.name
            }
        }
        for(var i = 0; i < styleShortcuts.length; i++) {
            let shortcut = styleShortcuts[i]
            if(e.key == shortcut.key) {
                console.log('ran: ' + shortcut.name)
                e.preventDefault()
                return shortcut.name
            }
        }

        return getDefaultKeyBinding(e)
    }
    
    return (
        <div>
            <Head>
                <title>{title ? title : ''} | nottte.me</title>
            </Head>
            <Typography variant='h1' color='primary'>
                {title}
            </Typography>
            <Editor
                editorState={editorState}
                handleKeyCommand={handleKeyCommand}
                onChange={onChange}
                keyBindingFn={keyBindingFn}
                customStyleMap={styleMap /* might want to do something like `styleMap !== {} ? styleMap : {}`, but this seems to work */}
            />
        </div>
        
    )

}

export default withRouter(Note)
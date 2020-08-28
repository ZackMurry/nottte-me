import { useRouter } from 'next/router'
import React, { useState, useEffect } from 'react'
import { Typography } from '@material-ui/core'
import Head from 'next/head'
import Cookie from 'js-cookie'
import { EditorState, Editor, convertFromRaw, convertToRaw, RichUtils, getDefaultKeyBinding, KeyBindingUtil, Modifier } from 'draft-js'

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

const testShortcuts = [
    {
        title: 'myShortcut',
        keyCode: 77,
        text: 'this is a shortcut'
    },
    {
        title: 'myOtherShortcut',
        keyCode: 80,
        text: '\t'
    }
]


//used for writing notes
//todo visual saving indicator
export default function Note() {
    
    const { hasCommandModifier } = KeyBindingUtil

    const router = useRouter()
    const { title } = router.query
    const jwt = Cookie.get('jwt')
    const [ editorState, setEditorState ] = useState(() => EditorState.createWithContent(emptyContentState))

    if(!jwt) {
        console.log('Unauthenticated')
    }

    useEffect(() => {
        if(title) {
            getFromServer()
        }
    }, [ title ])

    //think i'll need to use this to use the auto-save plugin
    const save = async () => {
        console.log('saving...')
        if(!jwt) return;
        const requestOptions = {
            method: 'PATCH',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt},
            body: JSON.stringify({
                title: title,
                body: convertToRaw(editorState.getCurrentContent())
            })
        }

        const response = await fetch('http://localhost:8080/api/v1/notes/save', requestOptions)
    }

    const saveWithNew = async (newEditorState) => {
        console.log('saving...')
        if(!jwt) return;
        const requestOptions = {
            method: 'PATCH',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt},
            body: JSON.stringify({
                title: title,
                body: convertToRaw(newEditorState.getCurrentContent())
            })
        }

        const response = await fetch('http://localhost:8080/api/v1/notes/save', requestOptions)
    }

    // used for keyboard shortcuts
    const handleKeyCommand = (command) => {
        //special key binds that are assigned from the start
        //todo make it so that users cannot make keybinds that start with nottte-
        if(command === 'nottte-save') {
            save()
        }

        for(var i = 0; i < testShortcuts.length; i++) {
            let shortcut = testShortcuts[i]
            if(shortcut.title == command) {
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
        const newState = RichUtils.handleKeyCommand(editorState, command)
        if (newState) {
          onChange(newState)
          return 'handled'
        }
        return 'not-handled'
    }

    const onChange = async (newEditorState) => {
        const oldContent = editorState.getCurrentContent()
        const newContent = newEditorState.getCurrentContent()
        await setEditorState(newEditorState)
        if(oldContent != newContent) {
            saveWithNew(newEditorState)
        }
    }
    
    const getFromServer = async () => {
        if(!jwt) return; //todo prompt sign in or un-auth'd note
        const requestOptions = {
            method: 'GET',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt}
        }
        const response = await fetch('http://localhost:8080/api/v1/notes/note/' + title + '/raw', requestOptions)
        const text = await response.text()

        //todo show error for 404s and 401s
        if(response.status === 401) return;
        if(response.status === 404) return;

        if(response === '') {
            setEditorState(EditorState.createEmpty())
        } else {
            const parsedText = JSON.parse(text)
            const textFromRaw = convertFromRaw(parsedText)
            const textEditorState = EditorState.createWithContent(textFromRaw)
            setEditorState(textEditorState)
        }
    }
    
    //e is a SyntheticKeyboardEvent. imagine being weakly typed
    const keyBindingFn = (e) => {
        //used for clarity and non-shortcut efficiency
        if(!hasCommandModifier(e)) {
            return getDefaultKeyBinding(e);
        }

        //todo prevent users from making CTRL + S shortcuts
        if(e.keyCode == '83') {
            e.preventDefault()
            return 'nottte-save'
        }
        
        for(var i = 0; i < testShortcuts.length; i++) {
            let shortcut = testShortcuts[i]
            if(e.keyCode == shortcut.keyCode) {
                console.log('ran: ' + shortcut.title)
                return shortcut.title
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
            />
        </div>
        
    )

}
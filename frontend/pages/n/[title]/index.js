import { useRouter } from 'next/router'
import React, { useState, useEffect, useCallback } from 'react'
import { Typography } from '@material-ui/core'
import Head from 'next/head'
import Cookie from 'js-cookie'
import {
    EditorState, convertFromRaw, convertToRaw, RichUtils, getDefaultKeyBinding, Modifier
} from 'draft-js'
import { debounce } from 'lodash'
import Link from 'next/link'
import Editor from 'draft-js-plugins-editor'
import createLinkifyPlugin from 'draft-js-linkify-plugin'
import GenerateStyleMenu from '../../../components/shortcuts/GenerateStyleMenu'

const jsondiffpatch = require('jsondiffpatch')

const linkifyPlugin = createLinkifyPlugin({
    component: props => (
        /* eslint-disable */
        <a
            {..._.omit(props, ['blockKey'])}
            style={{ textDecoration: 'underline' }}
            onClick={() => window.open(props.href)}
            alt={'Go to ' + props.href}
        />
        /* eslint-enable */
    )
})
const plugins = [ linkifyPlugin ]

//used because EditorState.createFromEmpty() was producing errors.
//just an empty content state
const emptyContentState = convertFromRaw({
    entityMap: {},
    blocks: [
        {
            text: '',
            key: 'nottte',
            type: 'unstyled',
            entityRanges: []
        }
    ]
})

//used for writing notes
//todo visual saving indicator
//todo collapse selection on shortcut (or preferably replace selected text)
export default function Note() {
    const router = useRouter()
    const { title } = router.query
    const jwt = Cookie.get('jwt')

    const [ editorState, setEditorState ] = useState(() => EditorState.createWithContent(emptyContentState))
    
    const [ lastSavedEditorState, setLastSavedEditorState ] = useState(() => EditorState.createWithContent(emptyContentState))

    const [ textShortcuts, setTextShortcuts] = useState([])
    const [ styleShortcuts, setStyleShortcuts ] = useState([])
    const [ styleMap, setStyleMap ] = useState({})

    const [ showGenerateStyleMenu, setShowGenerateStyleMenu ] = useState(false)
    const [ tempSelection, setTempSelection ] = useState(null)
    const [ generatedStyles, setGeneratedStyles ] = useState([])

    // right click override
    // const handleContext = (event) => {
    //     event.preventDefault();
    //     const xPos = event.pageX + "px";
    //     const yPos = event.pageY + "px";
    //     console.log('right click: ' + xPos + ', ' + yPos)
    // }

    //todo only get editorstate from server on first load
    const getFromServer = async () => {
        if (!jwt) return //todo prompt sign in or un-auth'd note

        const requestOptions = {
            method: 'GET',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt }
        }

        //getting style shortcuts
        //ideally, you'd want to get the editorState first so that it could load faster for the user
        //but without a stylemap, provided by the style shortcuts, the editor state gets loaded before stylemaps are applied,
        //and it isn't updated until a new style shortcut is applied :(
        const styleShortcutResponse = await fetch('http://localhost:8080/api/v1/users/principal/preferences/shortcuts/style', requestOptions)
        const styleShortcutText = await styleShortcutResponse.text()

        if (styleShortcutResponse.status === 401) return
        if (styleShortcutResponse.status === 403) return
        if (styleShortcutResponse.status === 404) return

        let parsedStyleShortcuts = JSON.parse(styleShortcutText)
        await setStyleShortcuts(parsedStyleShortcuts)
        console.log(styleShortcutText)

        //getting shared style shortcuts
        const sharedShortcutResponse = await fetch('http://localhost:8080/api/v1/shares/principal/shortcuts', requestOptions)

        console.log(sharedShortcutResponse.status)
        if (sharedShortcutResponse.status === 401) return
        if (sharedShortcutResponse.status === 403) return
        if (sharedShortcutResponse.status === 404) return

        const sharedShortcutText = await sharedShortcutResponse.text()
        const parsedSharedShortcuts = JSON.parse(sharedShortcutText)

        const generatedStyleResponse = await fetch('http://localhost:8080/api/v1/users/principal/preferences/shortcuts/generated', requestOptions)
        const generatedStyleText = await generatedStyleResponse.text()

        if (generatedStyleResponse.status !== 200) {
            console.log('generated style error: ' + generatedStyleResponse.status)
            return
        }

        const parsedGeneratedStyles = JSON.parse(generatedStyleText)
        setGeneratedStyles(parsedGeneratedStyles)

        parsedStyleShortcuts = [...parsedSharedShortcuts, ...parsedStyleShortcuts]

        let newStyleMap = {}
        /* eslint-disable */
        for (const styleShortcut of parsedStyleShortcuts) {
            const { name } = styleShortcut

            for (const styleAttribute of styleShortcut.attributes) {
                const { attribute } = styleAttribute
                const { value } = styleAttribute
                const existingAttributes = newStyleMap[name]
                newStyleMap = {
                    ...newStyleMap,
                    [name]: {
                        ...existingAttributes,
                        [attribute]: value
                    }
                }
            }
        }

        for (const genStyle of parsedGeneratedStyles) {
            const { name } = genStyle

            const { attribute } = genStyle.attribute
            const { value } = genStyle.attribute

            newStyleMap = {
                ...newStyleMap,
                [name]: {
                    [attribute]: value
                }
            }
        }
        /* eslint-enable */

        console.log('map: ' + JSON.stringify(newStyleMap))
        await setStyleMap(newStyleMap)

        //getting editor state

        const editorResponse = await fetch('http://localhost:8080/api/v1/notes/note/' + encodeURI(title) + '/raw', requestOptions)
        const editorText = await editorResponse.text()
        console.log(editorResponse.status)

        //todo show error for 404s and 401s
        if (editorResponse.status === 401) return
        if (editorResponse.status === 404) return
        if (editorResponse.status === 403) return
        if (editorResponse.status === 500) return

        if (editorResponse === '') {
            setEditorState(EditorState.createEmpty())
        } else {
            const parsedText = JSON.parse(editorText)
            const textFromRaw = convertFromRaw(parsedText)
            const textEditorState = EditorState.createWithContent(textFromRaw)
            setEditorState(textEditorState)
            setLastSavedEditorState(textEditorState)
        }

        //getting shortcuts

        const textShortcutResponse = await fetch('http://localhost:8080/api/v1/users/principal/preferences/shortcuts/text', requestOptions)
        let textShortcutText = await textShortcutResponse.text()

        //todo
        if (textShortcutResponse.status === 401) return
        if (textShortcutResponse.status === 404) return
        if (textShortcutResponse.status === 403) return

        //allowing for \n symbols in shortcuts to represent new lines and \t to represent tabs
        //todo maybe make custom replaceall function so that it can scan for all of the replaces in O(n) instead
        //of O(n*checks). can use a HashMap for that (or whatever it is in javascript)
        textShortcutText = textShortcutText.replaceAll('\\\\n', '\\n').replaceAll('\\\\t', '\\t')

        setTextShortcuts(JSON.parse(textShortcutText))
    }

    useEffect(() => {
        if (title) {
            getFromServer()
        }
        if (!jwt) {
            console.log('Unauthenticated')
            router.push('/login')
        }

        // for when i add custom right click. too early on to do that right now, though
        // document.addEventListener("contextmenu", handleContext);

        // return () => {
        //     document.removeEventListener('contextmenu', handleContext)
        // }
    }, [ title ])

    //saves once user stops typing for 1.5 seconds.
    //todo probly need to save less often (especially with selections counting as saves because of style saving)
    const debounceSave = useCallback(debounce(async newEditorState => {
        console.log('saving...')
        if (!jwt || !title) {
            console.log("can't save!")
            console.log('jwt: ' + jwt + '; title: ' + title)
            return
        }
        const requestOptions = {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt },
            body: JSON.stringify(convertToRaw(newEditorState.getCurrentContent()))
        }
        await fetch('http://localhost:8080/api/v1/notes/save/' + encodeURI(title), requestOptions)
        setLastSavedEditorState(newEditorState)
    }, 1500), [ jwt, title])

    const onChange = async newEditorState => {
        await setEditorState(newEditorState)
        let diff = jsondiffpatch.diff(
            convertToRaw(lastSavedEditorState.getCurrentContent()),
            convertToRaw(newEditorState.getCurrentContent())
        )
        console.log('raw diff: ' + JSON.stringify(diff))

        if (diff) {
            let newBlocks = []

            Object.keys(diff.blocks).forEach((item, index) => {
                if (item === '_t') return
                let block = diff.blocks['' + item]
                if (block['0']) {
                    console.log('0 detected')
                    block = block['0']
                } else {
                    if (block?.text) {
                        block.text = block.text[1]
                    }
                    if (block?.key) {
                        block.key = block.key[1]
                    }
                }
                console.log(item + ': ' + JSON.stringify(block))
                newBlocks[index] = {
                    idx: +item,
                    ...block
                }
            })
            console.log('newBlocks: ' + JSON.stringify(newBlocks))
            diff.blocks = newBlocks
            const requestOptions = {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt },
                body: JSON.stringify(diff)
            }
            await fetch('http://localhost:8080/api/v1/notes/principal/patch', requestOptions)
        }

        debounceSave(newEditorState)
    }

    const insertTextAtCursor = text => {
        const contentState = editorState.getCurrentContent()
        const targetRange = editorState.getSelection()
        const newContentState = Modifier.insertText(
            contentState,
            targetRange,
            text
        )
        const newEditorState = EditorState.push(
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
    const handleKeyCommand = command => {
        //special key binds that are assigned from the start
        //todo make it so that users cannot make keybinds that start with nottte-
        if (command === 'nottte-save') {
            debounceSave(editorState) //todo make this not a debounce
            return 'handled'
        } if (command === 'nottte-tab') {
            insertTextAtCursor('\t')
            return 'handled'
        }

        if (command === 'nottte-show-generate') return 'handled'

        //user can set shortcut name to __BLOCK-CLASS__ to enable some special block types
        if (command === '__center__') {
            setEditorState(RichUtils.toggleBlockType(editorState, 'center'))
            return 'handled'
        } if (command === '__right__') {
            setEditorState(RichUtils.toggleBlockType(editorState, 'right'))
            return 'handled'
        }

        //text shortcuts
        for (let i = 0; i < textShortcuts.length; i++) {
            const shortcut = textShortcuts[i]
            if (shortcut.name === command) {
                const contentState = editorState.getCurrentContent()
                const targetRange = editorState.getSelection()
                const newContentState = Modifier.insertText(
                    contentState,
                    targetRange,
                    shortcut.text
                )
                const newEditorState = EditorState.push(
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
        for (let i = 0; i < styleShortcuts.length; i++) {
            const shortcut = styleShortcuts[i]
            if (shortcut.name === command) {
                //calling on change because it waits for the new editor state to update first
                //else it would save the old editor state
                console.log('command: ' + shortcut.name)
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

    //e is a SyntheticKeyboardEvent. imagine being weakly typed
    const keyBindingFn = e => {
        if (e.key === 'Tab') {
            return 'nottte-tab'
        }

        //used for clarity and non-shortcut efficiency
        if (!e.ctrlKey) {
            return getDefaultKeyBinding(e)
        }

        //todo prevent users from making CTRL + S shortcuts
        //saving note with shortcut
        if (e.key === 's') {
            e.preventDefault()
            return 'nottte-save'
        }

        if (e.key === 'm') {
            setShowGenerateStyleMenu(!showGenerateStyleMenu)
            setTempSelection(editorState.getSelection())
            return 'nottte-show-generate'
        }

        for (let i = 0; i < textShortcuts.length; i++) {
            const shortcut = textShortcuts[i]
            if (e.key === shortcut.key && e.altKey === shortcut.alt) {
                console.log('ran: ' + shortcut.name)
                e.preventDefault()
                return shortcut.name
            }
        }
        for (let i = 0; i < styleShortcuts.length; i++) {
            const shortcut = styleShortcuts[i]
            if (e.key === shortcut.key && e.altKey === shortcut.alt) {
                console.log('ran: ' + shortcut.name)
                e.preventDefault()
                return shortcut.name
            }
        }

        return getDefaultKeyBinding(e)
    }

    const getBlockStyle = block => {
        switch (block.getType()) {
        case 'left':
            return 'align-left'
        case 'center':
            return 'align-center'
        case 'right':
            return 'align-right'
        default:
            return 'block'
        }
    }

    const handleCreateGeneratedStyle = async (attribute, value) => {
        console.log('attr: ' + attribute + ', val: ' + value)
        let generatedName = ''

        /* eslint-disable */
        for (const genStyle of generatedStyles) {
            console.log(JSON.stringify(generatedStyles[0]))
            if (genStyle.attribute.attribute == attribute && genStyle.attribute.value == value) {
                generatedName = genStyle.name
                setStyleMap({
                    ...styleMap,
                    [generatedName]: {
                        [attribute]: value
                    }
                })
            }
        }
        /* eslint-enable */

        if (generatedName === '') {
            const requestOptions = {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt },
                body: JSON.stringify({
                    attribute,
                    value
                })
            }

            const response = await fetch('http://localhost:8080/api/v1/users/principal/preferences/shortcuts/generated', requestOptions)

            if (response.status !== 200) {
                console.log('error: ' + response.status)
                return
            }

            generatedName = await response.text()
        }

        await setEditorState(
            RichUtils.toggleInlineStyle(
                EditorState.acceptSelection(
                    editorState,
                    tempSelection
                ),
                generatedName
            )
        )

        setShowGenerateStyleMenu(false)
    }

    return (
        <div>
            <Head>
                <title>
{title ? title + ' | ' : ''}
nottte.me
                </title>
            </Head>
            <Link href='/'>
                <Typography variant='h2' color='primary' style={{ margin: '1vh 1vw', cursor: 'pointer' }}>
                    nottte.me
                </Typography>
            </Link>

            <div style={{ width: '55%', minWidth: 750, margin: '10vh auto' }} className='note'>
                <div style={{ display: 'inline-flex', width: '100%' }}>
                    <Typography variant='h4' color='primary' style={{ width: '50%', fontWeight: 300 }}>
                        {title}
                    </Typography>
                    {/* not using next/link because you can't open it in a new tab (kind of defeats the point of next/link, anyways) */}
                    <a href={'/n/' + encodeURI(title) + '/settings'} style={{ width: '50%' }}>
                        <Typography
                            variant='h4'
                            style={{
                                fontWeight: 300, textAlign: 'right', color: '#d9d9d9', cursor: 'pointer'
                            }}
                        >
                            settings
                        </Typography>
                    </a>

                </div>

                <Editor
                    editorState={editorState}
                    handleKeyCommand={handleKeyCommand}
                    onChange={onChange}
                    keyBindingFn={keyBindingFn}
                    customStyleMap={styleMap}
                    editorKey='editor' //this fixes a 'props did not match' error
                    blockStyleFn={getBlockStyle}
                    plugins={plugins}
                />
            </div>

            {
                showGenerateStyleMenu && (
                    <div style={{
                        position: 'fixed', top: '15%', left: '0%', zIndex: 2, width: '100%'
                    }}
                    >
                        <GenerateStyleMenu onCreate={handleCreateGeneratedStyle} />
                    </div>
                )
            }

        </div>

    )
}

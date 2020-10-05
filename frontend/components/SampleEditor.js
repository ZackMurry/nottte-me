import React, { useState } from 'react'
import {
    Editor, EditorState, convertFromRaw, RichUtils, getDefaultKeyBinding, Modifier
} from 'draft-js'

//used because EditorState.createFromEmpty() was producing errors.
//just an empty content state
const emptyContentState = convertFromRaw({
    entityMap: {},
    blocks: [
        {
            // eslint-disable-next-line max-len
            text: 'Welcome to nottte.me. We allow you to fully customize your writing experience by creating custom text shortcuts, style shortcuts, and more. You can also easily export your notes to Google Docs, PDF, and HTML.',
            key: 'sample',
            type: 'unstyled',
            entityRanges: []
        }
    ]
})

//used on index for a preview
export default function SampleEditor({ style, step, moveToNextStep }) {
    const [ editorState, setEditorState ] = useState(() => EditorState.createWithContent(emptyContentState))
    const textShortcuts = [
        {
            name: 'insert-sample',
            key: 'l',
            text: "It's that easy!",
            alt: false
        }
    ]
    const styleShortcuts = [
        {
            name: 'yellow-highlight',
            key: 'q',
            attributes: [{
                'background-color': 'yellow'
            }],
            alt: false
        }
    ]
    const styleMap = {
        'yellow-highlight': {
            'background-color': 'yellow'
        }
    }

    const onChange = async newEditorState => {
        if (step === 1) {
            if (editorState.getCurrentContent() !== newEditorState.getCurrentContent()) {
                setTimeout(() => moveToNextStep(), 1500)
            }
        }
        setEditorState(newEditorState)
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
            onChange(editorState) //todo make this not a debounce
            return 'handled'
        } if (command === 'nottte-tab') {
            insertTextAtCursor('\t')
            return 'handled'
        }

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
                if (step === 2 && shortcut.name === 'insert-sample') {
                    setTimeout(() => moveToNextStep(), 2500)
                }
                return 'handled'
            }
        }

        //style shortcuts
        for (let k = 0; k < styleShortcuts.length; k++) {
            const shortcut = styleShortcuts[k]
            if (shortcut.name === command) {
                //calling on change because it waits for the new editor state to update first
                //else it would save the old editor state
                console.log('command: ' + shortcut.name)
                setEditorState(RichUtils.toggleInlineStyle(editorState, shortcut.name))
                if (step === 3 && shortcut.name === 'yellow-highlight') {
                    setTimeout(() => moveToNextStep(), 5000)
                }
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

    return (
        <div className='sample-div' style={{ ...style }}>
            <Editor
                editorState={editorState}
                handleKeyCommand={handleKeyCommand}
                onChange={onChange}
                keyBindingFn={keyBindingFn}
                customStyleMap={styleMap}
                editorKey='editor' //this fixes a 'props did not match' error
                blockStyleFn={getBlockStyle}
            />
        </div>

    )
}

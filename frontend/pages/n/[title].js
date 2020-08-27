import { useRouter } from 'next/router'
import React, { useState, useEffect } from 'react'
import { Typography } from '@material-ui/core'
import Head from 'next/head'
import Cookie from 'js-cookie'
import { EditorState, Editor, convertFromRaw, convertToRaw, RichUtils } from 'draft-js'

//used for writing notes
export default function Note() {
    
    const router = useRouter()
    const { title } = router.query
    const jwt = Cookie.get('jwt')
    const [ editorState, setEditorState ] = useState(() => EditorState.createEmpty())

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

        const response = await (await fetch('http://localhost:8080/api/v1/notes/save', requestOptions))
        console.log(response.text())
        console.log(response.status)
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

        const response = await (await fetch('http://localhost:8080/api/v1/notes/save', requestOptions))
    }

    const handleKeyCommand = (command) => {
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

    return (
        <div>
            <Head>
                <title>{title ? title : ''} | nottte.me</title>
                <link href="/styles/styles.css" rel="stylesheet" />
            </Head>
            <Typography variant='h1' color='primary'>
                {title}
            </Typography>
            <Editor
                editorState={editorState}
                handleKeyCommand={handleKeyCommand}
                onChange={onChange}
                editorClassName="editor"
            />
        </div>
        
    )

}
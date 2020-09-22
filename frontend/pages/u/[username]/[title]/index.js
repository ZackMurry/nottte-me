import { Typography } from '@material-ui/core'
import { convertFromRaw, Editor, EditorState } from 'draft-js'
import Head from 'next/head'
import Link from 'next/link'
import { useRouter } from 'next/router'
import React, { useEffect, useState } from 'react'
import Cookie from 'js-cookie'

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

export default function SharedNote() {

    const router = useRouter()
    const {username, title} = router.query

    const jwt = Cookie.get('jwt')

    const [ editorState, setEditorState ] = useState(() => EditorState.createWithContent(emptyContentState))
    const [ styleMap, setStyleMap ] = useState({})

    useEffect(() => {
        if(title) {
            getFromServer()
        }
        if(!jwt) {
            console.log('Unauthenticated')
            router.push('/login')
        }

        // for when i add custom right click. too early on to do that right now, though
        // document.addEventListener("contextmenu", handleContext);

        // return () => {
        //     document.removeEventListener('contextmenu', handleContext)
        // }

    }, [ title ])

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
        const styleShortcutResponse = await fetch(`http://localhost:8080/api/v1/shares/principal/note/${username}/${title}/shortcuts/style`, requestOptions)
        const styleShortcutText = await styleShortcutResponse.text()
        
        if(styleShortcutResponse.status === 401) return;
        if(styleShortcutResponse.status === 403) return;
        if(styleShortcutResponse.status === 404) return;
        
        const parsedStyleShortcuts = JSON.parse(styleShortcutText)
        console.log(styleShortcutText)

        let newStyleMap = {}
        for(var i = 0; i < parsedStyleShortcuts.length; i++) {
            let styleShortcut = parsedStyleShortcuts[i]
            let name = styleShortcut.name

            for(var j = 0; j < styleShortcut.attributes.length; j++) {
                let attribute = styleShortcut.attributes[j].attribute
                let value = styleShortcut.attributes[j].value
                let existingAttributes = newStyleMap[name]
                newStyleMap = {
                    ...newStyleMap,
                    [name]: {
                        ...existingAttributes,
                        [attribute]: value
                    }
                }
            }
        }
        console.log('map: ' + JSON.stringify(newStyleMap))
        await setStyleMap(newStyleMap)
        
        //getting editor state

        const editorResponse = await fetch(`http://localhost:8080/api/v1/shares/principal/note/${encodeURI(username)}/${encodeURI(title)}/raw`, requestOptions)
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
    }

    const getBlockStyle = (block) => {
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

    const onChange = () => {

    }

    return (
        <div>
            <Head>
                <title>{title ? title + ' | ' : ''}nottte.me</title>
            </Head>
            <Link href='/'>
                <Typography variant='h2' color='primary' style={{margin: '1vh 1vw', cursor: 'pointer'}}>
                    nottte.me
                </Typography>
            </Link>
            
            <div style={{width: '55%', minWidth: 750, margin: '10vh auto'}}>
                <div style={{display: 'inline-flex', width: '100%'}}>
                    <Typography variant='h4' color='primary' style={{width: '50%', fontWeight: 300}}>
                        {title}
                    </Typography>
                    {/* todo custom settings page for users accessing a shared note (need to change link) */}
                    <a href={'/u/' + encodeURI(username) + '/' + encodeURI(title) + '/settings'} style={{width: '50%'}}>
                        <Typography variant='h4' style={{fontWeight: 300, textAlign: 'right', color: '#d9d9d9', cursor: 'pointer'}}>
                            settings
                        </Typography>
                    </a>
                    
                </div>
                
                <Editor
                    editorState={editorState}
                    customStyleMap={styleMap}
                    editorKey='editor' //this fixes a 'props did not match' error
                    blockStyleFn={getBlockStyle}
                    readOnly={true}
                />
            </div>
            
        </div>
        
    )

}
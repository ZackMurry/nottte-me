import { Paper, Typography } from '@material-ui/core'
import Head from 'next/head'
import Link from 'next/link'
import { Router, useRouter, withRouter } from 'next/router'
import React, { useEffect, useState } from 'react'
import Navbar from '../../../../components/Navbar'
import Cookie from 'js-cookie'
import { convertFromRaw, EditorState } from 'draft-js'
import DownloadWithPreview from '../../../../components/DownloadWithPreview'
import draftToPdf from '../../../../components/DraftToPdf'
import openInNewTab from '../../../../components/openInNewTab'

function Export() {

    const router = useRouter()
    const { title } = router.query
    const jwt = Cookie.get('jwt')

    const [ editorState, setEditorState ] = useState('')
    const [ styleMap, setStyleMap ] = useState('')

    useEffect(() => {
        if(title && jwt) {
            getFromServer()
        }
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
        const styleMapResponse = await fetch('http://localhost:8080/api/v1/users/principal/preferences/shortcuts/style', requestOptions)
        const styleMapText = await styleMapResponse.text()
        
        if(styleMapResponse.status === 401) return;
        if(styleMapResponse.status === 403) return;
        if(styleMapResponse.status === 404) return;
        
        const parsedStyleMap = JSON.parse(styleMapText)
        await setStyleMap(parsedStyleMap)

        let newStyleMap = {}
        for(var i = 0; i < parsedStyleMap.length; i++) {
            let styleShortcut = parsedStyleMap[i]
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

    const downloadAsPdf = () => {
        draftToPdf(editorState.getCurrentContent(), styleMap, title, false)
    }

    const downloadAsDoc = () => {
        draftToPdf(editorState.getCurrentContent(), styleMap, title, true)
    }

    return (
        <div>
            <Head>
                <title>export {title} | nottte.me</title>
            </Head>
            <div style={{marginTop: 0}} >
                <Navbar />
            </div>

            {/* main login */}
            <Paper style={{margin: '20vh auto 15vh auto', padding: '10vh 0 25vh 0', marginTop: '20vh', width: '50%', borderRadius: 40, boxShadow: '5px 5px 10px black', minWidth: 500}} >
                <Typography variant='h1' style={{textAlign: 'center', padding: '2vh 0'}}>
                    Export note
                </Typography>

                {/* table of contents */}
                <div style={{width: '60%', margin: '0 auto'}}>
                    <Link href={'/n/' + title + '/settings/export#pdf'}>
                        <Typography variant='h5' style={{textAlign: 'center', cursor: 'pointer'}}>
                            Export as PDF
                        </Typography>
                    </Link>
                    <Link href={'/n/' + title + '/settings/export#docs'}>
                        <Typography variant='h5' style={{textAlign: 'center', cursor: 'pointer'}}>
                            Export to Google Docs
                        </Typography>
                    </Link>
                    <Link href={'/n/' + title + '/settings/export#html'}>
                        <Typography variant='h5' style={{textAlign: 'center', cursor: 'pointer'}}>
                            Export as HTML
                        </Typography>
                    </Link>
                </div>
                <div style={{paddingTop: '15%'}}></div>
                <div id='pdf' style={{paddingTop: '10vh', paddingBottom: '25vh', width: '80%', margin: '0 auto'}}>
                    <Typography variant='h4' style={{textAlign: 'center'}}>
                        Export as PDF
                    </Typography>
                    {
                        editorState && 
                        <DownloadWithPreview 
                            name={title} 
                            editorState={editorState} 
                            onClick={downloadAsPdf} 
                            styleMap={styleMap} 
                            blockStyleFn={getBlockStyle}
                            style={{margin: '5vh 5vw'}}
                        />
                    }
                </div>
                <div id='docs' style={{width: '80%', margin: '0 auto'}}>
                    <Typography variant='h4' style={{textAlign: 'center'}}>
                        Export to Google Docs
                    </Typography>
                    {/* todo explain why Courier in /help/export/courier */}
                    <Typography style={{margin: '3vh auto'}}>
                        To export to Google Docs, you have to first convert it to PDF form,
                        and then import it to Docs through Google Drive. Docs can't read some
                        of the characters in the Roboto font 
                        <span style={{textDecoration: 'underline', cursor: 'pointer', marginLeft: 4}} onClick={() => router.push('/help/export/courier')}>
                            very well
                        </span>
                        , so this export will
                        convert your font to Courier. You can change the font to anything once
                        you have your note in Docs.
                    </Typography>
                    <Typography variant='h5' style={{textAlign: 'center'}}>
                        Download as Docs-compatible PDF
                    </Typography>
                    {
                        editorState &&
                        <DownloadWithPreview
                            name={title}
                            editorState={editorState}
                            onClick={downloadAsDoc}
                            styleMap={styleMap}
                            blockStyleFn={getBlockStyle}
                        />
                    }
                    <Typography variant='h5' style={{textAlign: 'center', margin: '4vh 0 3vh 0'}}>
                        Import PDF to Google Docs
                    </Typography>
                    <Typography style={{margin: '3vh auto'}}>
                        Now, you'll have to go to 
                        <span style={{textDecoration: 'underline', cursor: 'pointer', margin: '0 4px'}} onClick={() => openInNewTab('http://drive.google.com')}>
                            Google Drive
                        </span>
                        and import the PDF. You can do this by clicking 'New' and then 'File Upload' 
                        (you can also drag the file into your Drive). 
                        Then, select your PDF and it should start importing. Once it's done uploading to Drive,
                        click on your PDF in Google Drive. Click the 'Open with' button at the top of the screen
                        and select 'Google Docs'.
                    </Typography>
                    
                    <img 
                        src='/pdf-to-doc.png' 
                        alt="Screenshot of 'Open with' screen" 
                        style={{width: '80%', display: 'block', margin: '0 auto', minWidth: 100}} 
                    />

                    <Typography>
                        Finally, you'll see that your note has been successfully exported
                        to Google Docs!
                    </Typography>
                    
                    <img
                        src='/exported-to-docs.png'
                        alt="Screenshot of note as a Google Doc"
                        style={{width: '80%', display: 'block', margin: '0 auto', minWidth: 100}}
                    />

                </div>
            </Paper>
        </div>
        
    )

}

export default withRouter(Export)

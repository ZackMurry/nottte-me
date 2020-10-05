import { Paper, Typography } from '@material-ui/core'
import Head from 'next/head'
import Link from 'next/link'
import { useRouter } from 'next/router'
import React, { useEffect, useState } from 'react'
import Cookie from 'js-cookie'
import { convertFromRaw, EditorState } from 'draft-js'
import Navbar from '../../../../components/Navbar'
import DownloadWithPreview from '../../../../components/notes/export/DownloadWithPreview'
import draftToPdf from '../../../../components/notes/export/DraftToPdf'
import openInNewTab from '../../../../components/utils/OpenInNewTab'
import draftToHtml from '../../../../components/notes/export/DraftToHtml'

export default function Export() {
    const router = useRouter()
    const { title } = router.query
    const jwt = Cookie.get('jwt')

    const [ editorState, setEditorState ] = useState('')
    const [ styleMap, setStyleMap ] = useState('')
    const [ html, setHtml ] = useState('')

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
        const styleMapResponse = await fetch('http://localhost:8080/api/v1/users/principal/preferences/shortcuts/style', requestOptions)
        const styleMapText = await styleMapResponse.text()

        if (styleMapResponse.status === 401) return
        if (styleMapResponse.status === 403) return
        if (styleMapResponse.status === 404) return

        const parsedStyleMap = JSON.parse(styleMapText)
        await setStyleMap(parsedStyleMap)

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
            setHtml(draftToHtml(textEditorState.getCurrentContent(), parsedStyleMap))
        }
    }

    useEffect(() => {
        if (title && jwt) {
            getFromServer()
        }
    }, [ title ])

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

    const downloadAsPdf = () => {
        draftToPdf(editorState.getCurrentContent(), styleMap, title)
    }

    return (
        <div>
            <Head>
                <title>
export
{title}
{' '}
| nottte.me
                </title>
            </Head>
            <div style={{ marginTop: 0 }}>
                <Navbar />
            </div>

            {/* main login */}
            <Paper style={{
                margin: '20vh auto 15vh auto',
                padding: '5vh 0 25vh 0',
                marginTop: '20vh',
                width: '50%',
                borderRadius: 40,
                boxShadow: '5px 5px 10px black',
                minWidth: 500
            }}
            >
                <Typography variant='h1' style={{ textAlign: 'center', padding: '2vh 0' }}>
                    Export note
                </Typography>

                {/* table of contents */}
                <div style={{ width: '60%', margin: '0 auto' }}>
                    <Link href={'/n/' + title + '/settings/export#pdf'}>
                        <Typography variant='h5' style={{ textAlign: 'center', cursor: 'pointer' }}>
                            Export as PDF
                        </Typography>
                    </Link>
                    <Link href={'/n/' + title + '/settings/export#docs'}>
                        <Typography variant='h5' style={{ textAlign: 'center', cursor: 'pointer' }}>
                            Export to Google Docs
                        </Typography>
                    </Link>
                    <Link href={'/n/' + title + '/settings/export#html'}>
                        <Typography variant='h5' style={{ textAlign: 'center', cursor: 'pointer' }}>
                            Export as HTML
                        </Typography>
                    </Link>
                </div>
                <div style={{ paddingTop: '15%' }} />
                <div
                    id='pdf'
                    style={{
                        paddingTop: '10vh', paddingBottom: '25vh', width: '80%', margin: '0 auto'
                    }}
                >
                    <Typography variant='h4' style={{ textAlign: 'center' }}>
                        Export as PDF
                    </Typography>
                    {
                        editorState
                        && (
<DownloadWithPreview
    name={title}
    editorState={editorState}
    onClick={downloadAsPdf}
    styleMap={styleMap}
    blockStyleFn={getBlockStyle}
    style={{ margin: '5vh 5vw' }}
/>
                        )
                    }
                </div>
                <div id='docs' style={{ width: '80%', margin: '0 auto' }}>
                    <Typography variant='h4' style={{ textAlign: 'center' }}>
                        Export to Google Docs
                    </Typography>
                    {/* todo explain why Courier in /help/export/courier */}
                    <Typography style={{ margin: '3vh auto' }}>
                        To export to Google Docs, you have to first convert it to PDF form,
                        and then import it to Docs through Google Drive. For some combinations of characters,
                        this won't work, so you'll have to read further for full compatibility.
                    </Typography>
                    <Typography variant='h5' style={{ textAlign: 'center' }}>
                        First, download as PDF
                    </Typography>
                    {
                        editorState
                        && (
<DownloadWithPreview
    name={title}
    editorState={editorState}
    onClick={downloadAsPdf}
    styleMap={styleMap}
    blockStyleFn={getBlockStyle}
/>
                        )
                    }
                    <Typography variant='h5' style={{ textAlign: 'center', margin: '4vh 0 3vh 0' }}>
                        Import PDF to Google Docs
                    </Typography>
                    <Typography style={{ margin: '3vh auto' }}>
                        Now, you'll have to go to
                        <span
                            style={{
                                textDecoration: 'underline',
                                cursor: 'pointer',
                                margin: '0 4px'
                            }}
                            onClick={() => openInNewTab('http://drive.google.com')}
                        >
                            Google Drive
                        </span>
                        and import the PDF. You can do this by clicking 'New' and then 'File Upload'
                        (you can also drag the file into your Drive).
                        Then, select your PDF and it should start importing. Once it's done uploading to Drive,
                        click on your PDF in Google Drive. Click the 'Open with' button at the top of the screen
                        and select 'Google Docs'.
                    </Typography>

                    <img
                        src='/pdf-to-doc-min.png'
                        alt="Screenshot of 'Open with' screen"
                        style={{
                            width: '80%', display: 'block', margin: '0 auto', minWidth: 100
                        }}
                    />

                    <Typography>
                        Finally, you'll see that your note has been successfully exported
                        to Google Docs!
                    </Typography>

                    <img
                        src='/exported-to-docs-min.png'
                        alt='Screenshot of note as a Google Doc'
                        style={{
                            width: '80%', display: 'block', margin: '0 auto', minWidth: 100
                        }}
                    />

                    <Typography variant='h5' style={{ textAlign: 'center', margin: '4vh 0 3vh 0' }}>
                        Special cases
                    </Typography>

                    <Typography variant='h6' style={{ textAlign: 'center', margin: '4vh 0 3vh 0' }}>
                        Ligatures
                    </Typography>

                    <Typography style={{ margin: '3vh auto' }}>
                        As indicated earlier, there are some compatibility issues. If you type special
                        pairs of letters like "fl", the conversion will truncate them into one letter
                        (in this example, "f"). They're called 'ligatures'. This problem can, however, be fixed.
                    </Typography>
                    <Typography style={{ margin: '1vh auto' }}>
                        You'll have to convert your PDF to a .docx (Microsoft Word) file before importing it to Google Docs.
                        You can use a website like
                        <span
                            style={{
                                cursor: 'pointer',
                                textDecoration: 'underline',
                                margin: '0 3px'
                            }}
                            onClick={() => openInNewTab('https://www.ilovepdf.com/pdf_to_word')}
                        >
                            ilovepdf.com
                        </span>
                        to do just this. Afterwards, upload it to Google Drive and open it with Docs,
                        as shown before. Finally, in the top-left corner of the Doc, click 'File' and 'Save as Google Docs'.
                    </Typography>

                    <Typography variant='h6' style={{ textAlign: 'center', margin: '4vh 0 3vh 0' }}>
                        Formatting
                    </Typography>

                    <Typography style={{ margin: '1vh auto' }}>
                        Your Doc might have several formatting errors.
                        There's not much we can do about them behind the scenes, but there are easy fixes for all of the discovered issues.
                    </Typography>

                    <Typography style={{ margin: '1vh auto' }}>
                        First, if your text seems to wrap in the middle of the line instead of at the end,
                        adjust the indents by moving the errors on the ruler near the top.
                        The left arrow signifies the left indent and the right arrow will adjust the right indent.
                    </Typography>

                    <Typography style={{ margin: '1vh auto' }}>
                        Your Doc might also have several unwanted fonts. The quickest way to fix this is
                        to select all of the content on your document (by pressing Control and A) and changing the font to one that you like.
                    </Typography>

                    <Typography style={{ margin: '1vh auto' }}>
                        The spacing between lines might also have been converted poorly.
                        To fix this, you can select the affected areas and click "line spacing"
                        (on the formatting bar) and adjust it to your liking (default is 1.15).
                    </Typography>

                </div>
                {/* todo exporting to word */}
                <div id='html' style={{ width: '80%', margin: '15vh auto' }}>
                    <Typography variant='h4' style={{ textAlign: 'center', margin: '2vh 0' }}>
                        Export as HTML
                    </Typography>

                    <Typography>
                        Below is the raw HTML of your note. You can use this to add your note to your website
                        or export it to an unsupported program.
                    </Typography>

                    <Paper elevation={0} style={{ margin: '3vh auto' }}>
                        <Typography style={{ fontWeight: 300 }}>
                            {html}
                        </Typography>
                    </Paper>

                </div>

            </Paper>
        </div>

    )
}

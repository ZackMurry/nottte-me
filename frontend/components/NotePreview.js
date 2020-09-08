import React, { useEffect, useState } from 'react'
import { Paper, Typography, Card, CardContent, CardActions } from '@material-ui/core'
import { useRouter, withRouter } from 'next/router'
import { EditorState, convertFromRaw } from 'draft-js'

export default function NotePreview({ name, editorState }) {

    const [ rawText, setRawText] = useState('')
    const router = useRouter()

    useEffect(() => {
        if(editorState) {
            
            let raw = convertFromRaw(JSON.parse(editorState))
            let objEditorState = EditorState.createWithContent(raw)
            let currentContent = objEditorState.getCurrentContent()
            let plainText = currentContent.getPlainText('') + ''
            setRawText(plainText)
            console.log(plainText)
        }
    }, [ editorState ])

    const goToNotePage = () => {
        router.push('/n/' + encodeURI(name))
    }

    return (
        <div style={{margin: 0, cursor: 'pointer'}} onClick={() => goToNotePage()}>
            <Card>
                <CardContent>
                    <div style={{backgroundColor: '#2d323e', width: '100%', height: '25vh', borderRadius: 10, margin: 0, padding: 0, display: 'flex'}}>
                        <Paper elevation={0} style={{width: '60%', marginLeft: 'auto', marginRight: 'auto', marginBottom: 0, height: '60%', verticalAlign: 'bottom', alignSelf: 'flex-end', borderRadius: '10px 10px 0 0', overflow: 'hidden'}}>
                            <Typography style={{margin: 10, width: '100%', height: '92.5%', overflowY: 'scroll', paddingRight: 3, boxSizing: 'content-box'}}>
                                { rawText }
                            </Typography>
                        </Paper>
                    </div>
                </CardContent>
                <CardActions>
                    <Typography variant='h4' style={{marginLeft: 10}}>{ name }</Typography>
                </CardActions>
                
            </Card>
            
        </div>
        
    )

}

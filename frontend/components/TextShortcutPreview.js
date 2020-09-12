import React, { useState, useEffect } from 'react'
import { Typography, Grid, IconButton, TextField } from '@material-ui/core'
import EditIcon from '@material-ui/icons/Edit';
import DoneIcon from '@material-ui/icons/Done';


//todo deleting
export default function TextShortcutPreview({ name, button, text, update, jwt, onError, showError }) {

    const [ editMode, setEditMode ] = useState(false)
    const [ editedKey, setEditedKey ] = useState(button)
    const [ editedText, setEditedText ] = useState(text)

    const handleDone = async () => {
        setEditMode(false)

        //checking if anything has been changed
        if(editedKey == button && editedText == text) return
        
        //sending to server
        const requestOptions = {
            method: 'PATCH',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt},
            body: JSON.stringify({
                name: name,
                key: editedKey,
                text: editedText
            })
        }

        const response = await fetch('http://localhost:8080/api/v1/users/principal/preferences/shortcuts/text/' + encodeURI(name), requestOptions)
        console.log(response.status)

        if(response.status == 400) {
            onError('Unable to update text shortcut: error code 400')
            showError(true)
            return
        }

        update(name, editedKey, editedText)
    }

    const handleKeyDown = (e) => {
        setEditedKey(e.key)
        e.preventDefault()
    }

    //automatically finishes editing when enter is pressed on text field
    const handleEnterDetection = (e) => {
        if(e.key == 'Enter') {
            handleDone()
        }
    }

    return (
        <div>
            {
                editMode 
                ?
                (
                    <Grid container spacing={3}>
                        <Grid item xs={12} lg={4}>
                            <Typography>
                                {name}
                            </Typography>
                        </Grid>
                        <Grid item xs={12} lg={2} style={{paddingTop: 0, paddingBottom: 0}}>
                        <TextField 
                            value={editedKey} 
                            style={{width: '100%', caretColor: 'transparent'}} 
                            spellCheck='false'
                            onKeyDown={handleKeyDown}
                        />
                        </Grid>
                        <Grid item xs={12} lg={5} style={{paddingTop: 0, paddingBottom: 0}}>
                            <TextField 
                                value={editedText}
                                onKeyDown={e => handleEnterDetection(e)}
                                onChange={e => setEditedText(e.target.value)}
                                style={{padding: 0, width: '100%'}}
                                error={editedText.length <= 0 }
                                helperText={editedText.length <= 0 ? 'the text should not be empty' : ''}
                                rowsMax={10}
                                multiline
                            />
                        </Grid>
                        <Grid item xs={12} lg={1}>
                            <IconButton onClick={handleDone} style={{padding: 0}}>
                                <DoneIcon color='secondary' />
                            </IconButton>
                        </Grid>
                    </Grid>
                )
                :
                (
                    <Grid container spacing={3}>
                        <Grid item xs={12} lg={4}>
                            <Typography>{ name }</Typography>
                        </Grid>
                        <Grid item xs={12} lg={2}>
                            <Typography style={{fontWeight: 500}}>CTRL + { button }</Typography>
                        </Grid>
                        <Grid item xs={12} lg={5}>
                            <Typography>{ text.replaceAll('\\n', '\n').replaceAll('\\t', '\t') }</Typography>
                        </Grid>
                        <Grid item xs={12} lg={1}>
                            <IconButton onClick={() => setEditMode(true)} style={{padding: 0}}>
                                <EditIcon color='secondary' />
                            </IconButton>
                        </Grid>
                    </Grid>
                )
            }
            
                
                
        </div>

        
    )

}
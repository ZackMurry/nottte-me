import React, { useState } from 'react'
import { Grid, Typography, TextField, Button, makeStyles } from '@material-ui/core'
import theme from './theme'
import Router from 'next/router'
import PlainTooltip from './PlainTooltip'

const defaultName = 'myShortcutTitle'
const defaultKey = 'none'
const defaultText = 'shortcut text'

//todo show user error codes
export default function CreateTextShortcut({ jwt }) {

    const [ name, setName ] = useState(defaultName)
    const [ key, setKey ] = useState(defaultKey)
    const [ text, setText ] = useState(defaultText)
    const [ createError, setCreateError ] = useState('')

    const handleKeyPress = (e) => {
        setKey(e.key)
    }

    const handleCreate = async () => {
        console.log(name + ', ' + key + ', ' + text)
        if(!jwt) { 
            setCreateError('You have to be signed in to create a shortcut')
            return
        }

        const requestOptions = {
            method: 'POST',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt},
            body: JSON.stringify({
                name: name,
                key: key,
                text: text
            })
        }

        const response = await fetch('http://localhost:8080/api/v1/users/principal/preferences/shortcuts/text', requestOptions)
        console.log(response.status)
        if(response.status == 200) {
            Router.reload()
        }
    }

    return (
        <Grid container spacing={3}>
            <Grid item xs={4}>
                <TextField 
                    value={name} 
                    onChange={e => setName(e.target.value)} 
                    style={{width: '100%'}} 
                    helperText={name.length < 4 ? 'the name must be more than three characters' : 'name of your new shortcut'}
                    error={name.length < 4}
                    spellCheck='false'
                />
            </Grid>
            <Grid item xs={2}>
                <TextField 
                    value={key} 
                    onKeyPress={handleKeyPress} 
                    style={{width: '100%'}} 
                    helperText='key to use with control. just click and hit a key' 
                    error={key == defaultKey && (name != defaultName || text != defaultText)}
                    spellCheck='false'
                />
            </Grid>
            <Grid item xs={6}>
                <TextField 
                    value={text} 
                    onChange={e => setText(e.target.value)} 
                    style={{width: '100%'}} 
                    helperText={text == '' ? 'text cannot be empty' : 'text to insert when shortcut pressed'}
                    spellCheck='false'
                    error={text == ''}
                />
            </Grid>
            
            {/* new line */}
            <Grid item xs={12} style={{display: 'flex'}}>
                {/* todo tooltip when disabled */}
                <PlainTooltip disabled={!(name.length < 4 || key == defaultKey || text == '')} title={"this shortcut is invalid" + (key == defaultKey ? ". it might be because the key you have selected isn't a valid key" : '')} placement='right'>
                    <div style={{margin: '1% auto'}}>
                        {/* todo figure out how to make this not wrap on medium-ish screens */}
                        <Button 
                            color='primary'
                            style={{backgroundColor: (name.length < 4 || key == 'none' || text == '') ? '#fff' : theme.palette.secondary.main, padding: '1%'}} 
                            disabled={name.length < 4 || key == defaultKey || text == ''}
                            onClick={handleCreate}
                        >
                            Create shortcut
                        </Button>
                    </div>
                </PlainTooltip>
            </Grid>

        </Grid>
        
    )

}
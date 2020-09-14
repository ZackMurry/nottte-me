import React, { useState } from 'react'
import { Grid, TextField, Button, Typography } from '@material-ui/core'
import theme from './theme'
import Router from 'next/router'
import PlainTooltip from './PlainTooltip'

const defaultName = 'myStyleTitle'
const defaultKey = 'none'
const defaultAttribute = 'border'
const defaultValue = '4px black solid'

const nameError = 'two shortcuts cannot have the same name'
const miscError = "there was an error while creating a shortcut (this could be a server error). please double-check the shortcut's values"
const contactError = "the server couldn't be reached"

//todo show user error codes
//verrry similar to CreateTextShortcut
export default function CreateStyleShortcut({ jwt }) {

    const [ name, setName ] = useState(defaultName)
    const [ key, setKey ] = useState(defaultKey)
    const [ attribute, setAttribute ] = useState(defaultAttribute)
    const [ value, setValue ] = useState(defaultValue)

    const [ createError, setCreateError ] = useState('')
    const [ hoveringOverCreateButton, setHoveringOverCreateButton ] = useState(false) //used for tooltip

    const handleKeyDown = (e) => {
        setKey(e.key)
        e.preventDefault()
    }

    const handleCreate = async () => {
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
                attribute: attribute,
                value: value
            })
        }

        const response = await fetch('http://localhost:8080/api/v1/users/principal/preferences/shortcuts/style', requestOptions)

        if(response.status == 200) {
            Router.reload()
        } else if(response.status == 412) {
            //412 is PRECONDITION_FAILED; used if a shortcut with that name already exists
            setCreateError(nameError)
        } else if(response.status == 400) {
            //thrown by an SQLException in backend. realistically shouldn't happen
            setCreateError(miscError)
        } else if(response.status >= 500) {
            //thrown if cannot reach server
            setCreateError(contactError)
        }
    }

    return (
        <Grid container spacing={3}>
            <Grid item xs={3}>
                <TextField 
                    value={name} 
                    onChange={e => setName(e.target.value)} 
                    style={{width: '100%'}} 
                    helperText={name.length < 4 ? 'the name must be more than three characters' : (createError ? createError : 'name of your new shortcut')}
                    error={name.length < 4 || !!createError}
                    spellCheck='false'
                />
            </Grid>
            <Grid item xs={2}>
                <TextField 
                    value={key} 
                    style={{width: '100%', caretColor: 'transparent'}} 
                    helperText='key to use with control. just click and hit a key' 
                    error={key == defaultKey && (name != defaultName || attribute != defaultAttribute)}
                    spellCheck='false'
                    onKeyDown={handleKeyDown}
                />
            </Grid>
            <Grid item xs={4}>
                <TextField 
                    value={attribute} 
                    onChange={e => setAttribute(e.target.value)} 
                    style={{width: '100%'}} 
                    helperText={attribute == '' ? 'attribute cannot be empty' : 'CSS attribute to modify'}
                    spellCheck='false'
                    error={attribute == ''}
                />
            </Grid>
            <Grid item xs={3}>
                <TextField 
                    value={value} 
                    onChange={e => setValue(e.target.value)} 
                    style={{width: '100%'}} 
                    helperText={value == '' ? 'value cannot be empty' : 'value to apply to CSS attribute'}
                    spellCheck='false'
                    error={value == ''}
                />
            </Grid>
            
            {/* preview of style — shows what it will look like in the note */}
            <Grid item xs={12}>
                <div style={{textAlign: 'center', marginTop: '3%'}}>
                    <Typography variant='h4' style={{
                        [attribute]: value
                    }}>
                        Preview
                    </Typography>
                </div>
            </Grid>

            {/* new line */}
            <Grid item xs={12} style={{display: 'flex'}}>
                <div 
                    onMouseEnter={() => setHoveringOverCreateButton(true)} 
                    onMouseLeave={() => setHoveringOverCreateButton(false)}  
                    style={{margin: '1% auto'}}
                >
                    {/* todo tooltip when disabled */}
                    <PlainTooltip 
                        open={(name.length < 4 || key == defaultKey || attribute == '') && hoveringOverCreateButton} 
                        title={"this shortcut is invalid" 
                            + (key == defaultKey ? ". it might be because the key you have selected isn't a valid key" : '')} 
                        placement='right'
                    >
                        <div> {/* div here to allow for tooltips even when the button is disabled*/}
                            {/* todo figure out how to make this not wrap on medium-ish screens */}
                            <Button 
                                color='primary'
                                style={{backgroundColor: (name.length < 4 || key == 'none' || attribute == '') ? '#fff' : theme.palette.secondary.main, padding: '1vh', maxHeight: '5vh'}} 
                                disabled={name.length < 4 || key == defaultKey || attribute == ''}
                                onClick={handleCreate}
                            >
                                Create shortcut
                            </Button>
                        </div>
                    </PlainTooltip>
                </div>
            </Grid>

        </Grid>
        
    )

}

import React, { useState } from 'react'
import { Grid, TextField, Button } from '@material-ui/core'
import Router from 'next/router'
import theme from '../../theme'
import PlainTooltip from '../../utils/PlainTooltip'

const defaultName = 'myShortcutTitle'
const defaultKey = 'none'
const defaultText = 'shortcut text'

const nameError = 'two shortcuts cannot have the same name'
const miscError = "there was an error while creating a shortcut (this could be a server error). please double-check the shortcut's values"
const contactError = "the server couldn't be reached"

//todo show user error codes
export default function CreateTextShortcut({ jwt }) {
    const [ name, setName ] = useState(defaultName)
    const [ key, setKey ] = useState(defaultKey)
    const [ text, setText ] = useState(defaultText)
    const [ alt, setAlt ] = useState(false)

    const [ createError, setCreateError ] = useState('')
    const [ hoveringOverCreateButton, setHoveringOverCreateButton ] = useState(false) //used for tooltip

    const handleKeyDown = e => {
        setKey(e.key)
        setAlt(e.altKey)
        e.preventDefault()
    }

    const handleCreate = async () => {
        console.log(name + ', ' + key + ', ' + text)
        if (!jwt) {
            setCreateError('You have to be signed in to create a shortcut')
            return
        }

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt },
            body: JSON.stringify({
                name,
                key,
                text,
                alt
            })
        }

        const response = await fetch('http://localhost:8080/api/v1/users/principal/preferences/shortcuts/text', requestOptions)
        console.log(response.status)
        if (response.status === 200) {
            Router.reload()
        } else if (response.status === 412) {
            //412 is PRECONDITION_FAILED — used if the new shortcut has the same name as another
            setCreateError(nameError)
        } else if (response.status === 400) {
            setCreateError(miscError)
        } else if (response.status >= 500) {
            //if there's a server error
            setCreateError(contactError)
        }
    }

    return (
        <Grid container spacing={3}>
            <Grid item xs={4}>
                <TextField
                    value={name}
                    onChange={e => setName(e.target.value)}
                    style={{ width: '100%' }}
                    helperText={name.length < 4 ? 'the name must be more than three characters' : (createError || 'name of your new shortcut')}
                    error={name.length < 4 || !!createError}
                    spellCheck='false'
                />
            </Grid>
            <Grid item xs={2}>
                <TextField
                    value={(alt ? 'ALT+' : '') + key}
                    style={{ width: '100%', caretColor: 'transparent' }}
                    helperText='key to use with control. just click and hit a key'
                    error={key === defaultKey && (name !== defaultName || text !== defaultText)}
                    spellCheck='false'
                    onKeyDown={handleKeyDown}
                />
            </Grid>
            <Grid item xs={6}>
                <TextField
                    value={text}
                    onChange={e => setText(e.target.value)}
                    style={{ width: '100%' }}
                    helperText={text === '' ? 'text cannot be empty' : 'text to insert when shortcut pressed'}
                    spellCheck='false'
                    error={text === ''}
                />
            </Grid>

            {/* new line */}
            <Grid item xs={12} style={{ display: 'flex' }}>
                <div
                    onMouseEnter={() => setHoveringOverCreateButton(true)}
                    onMouseLeave={() => setHoveringOverCreateButton(false)}
                    style={{ margin: '1% auto' }}
                >
                    {/* todo tooltip when disabled */}
                    <PlainTooltip
                        open={(name.length < 4 || key === defaultKey || text === '') && hoveringOverCreateButton}
                        title={'this shortcut is invalid'
                            + (key === defaultKey ? ". it might be because the key you have selected isn't a valid key" : '')}
                        placement='right'
                    >
                        <div>
                            {' '}
                            {/* div here to allow for tooltips even when the button is disabled*/}
                            {/* todo figure out how to make this not wrap on medium-ish screens */}
                            <Button
                                color='primary'
                                style={{
                                    backgroundColor: (name.length < 4 || key === 'none' || text === '')
                                        ? '#fff'
                                        : theme.palette.secondary.main,
                                    padding: '1vh',
                                    maxHeight: '5vh'
                                }}
                                disabled={name.length < 4 || key === defaultKey || text === ''}
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

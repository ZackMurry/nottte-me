import {
    Grow, IconButton, TextField, Typography
} from '@material-ui/core'
import { useRouter } from 'next/router'
import React, { useEffect, useState } from 'react'
import DoneIcon from '@material-ui/icons/Done'
import PlainTooltip from '../utils/PlainTooltip'

export default function EditEmail({ jwt, currentEmail }) {
    const router = useRouter()

    const [ editedEmail, setEditedEmail ] = useState(currentEmail)

    useEffect(() => {
        setEditedEmail(currentEmail)
    }, [ currentEmail ])

    const handleSubmit = async () => {
        if (!jwt) router.push(`/login?redirect=${encodeURI('/account')}`)

        if (currentEmail === editedEmail) return

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt },
            body: editedEmail
        }

        const response = await fetch('http://localhost:8080/api/v1/users/principal/email', requestOptions)
        if (response.status === 200) router.reload()
        else {
            console.log(response.status)
        }
    }

    const enterDetection = key => {
        if (key === 'Enter') {
            handleSubmit()
        }
    }

    return (
        <div style={{ display: 'inline-flex' }}>
            <Typography style={{ marginTop: 4, marginRight: 5 }}>
                Email:
            </Typography>
            <TextField
                value={editedEmail}
                onChange={e => setEditedEmail(e.target.value)}
                onKeyDown={e => enterDetection(e.key)}
                helperText='To remove, make this empty.'
            />

            {
                editedEmail !== currentEmail && (
                    <PlainTooltip title='update email'>
                        <Grow
                            in
                            timeout={500}
                        >
                            <IconButton onClick={handleSubmit} style={{ width: 24, height: 24 }}>
                                <DoneIcon fontSize='small' />
                            </IconButton>
                        </Grow>
                    </PlainTooltip>
                )
            }

        </div>

    )
}

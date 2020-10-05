import { Button, TextField } from '@material-ui/core'
import { useRouter } from 'next/router'
import React, { useEffect, useState } from 'react'
import PlainTooltip from '../utils/PlainTooltip'

export default function RenameNoteField({ name, jwt, shared = false }) {
    console.log('shared: ' + shared)
    const router = useRouter()

    const [ editedName, setEditedName ] = useState(name || router.query.name)

    useEffect(() => {
        setEditedName(name)
    }, [ name ])

    const handleRename = async () => {
        if (name === editedName) return

        const requestOptions = {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt }
        }

        const response = await fetch(
            `http://localhost:8080/api/v1/notes/principal/note/${encodeURI(name)}/rename/${encodeURI(editedName)}`,
            requestOptions
        )
        if (response.status === 200) {
            router.push(`/n/${editedName}/settings#rename`)
        }
    }

    return (
        <div style={{ margin: 10 }}>
            <TextField
                value={editedName || ''}
                onChange={e => setEditedName(e.target.value)}
                style={{ margin: 5 }}
            />
            <PlainTooltip title='You cannot rename a shared note' disableHoverListener={shared ? undefined : true}>
                <div style={{ display: 'inline-flex' }}>
                    <Button
                        color='secondary'
                        variant='contained'
                        onClick={handleRename}
                        style={{ margin: 5 }}
                        disabled={shared ? true : undefined}
                    >
                        Rename note
                    </Button>
                </div>

            </PlainTooltip>

        </div>

    )
}

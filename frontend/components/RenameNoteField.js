import { Button, TextField } from '@material-ui/core'
import { useRouter } from 'next/router'
import React, { useEffect, useState } from 'react'

export default function RenameNoteField({ name, jwt }) {

    const router = useRouter()

    const [ editedName, setEditedName ] = useState(name ? name : router.query.name)

    useEffect(() => {
        setEditedName(name)
    }, [ name ])

    const handleRename = async () => {
        if(name == editedName) return

        const requestOptions = {
            method: 'PATCH',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt}
        }

        const response = await fetch(`http://localhost:8080/api/v1/notes/principal/note/${encodeURI(name)}/rename/${encodeURI(editedName)}`, requestOptions)
        if(response.status == 200) {
            router.push(`/n/${editedName}/settings#rename`)
        }
    }

    return (
        <div style={{margin: 10}}>
            <TextField
                value={editedName ? editedName : ''}
                onChange={e => setEditedName(e.target.value)}
                style={{margin: 5}}

            />
            <Button color='secondary' variant='contained' onClick={handleRename} style={{margin: 5}}>
                Rename note
            </Button>
        </div>
        
    )

}
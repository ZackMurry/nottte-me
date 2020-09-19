import { Button } from '@material-ui/core'
import { useRouter } from 'next/router'
import React, { useState } from 'react'
import YesNoDialog from './YesNoDialog'

export default function DeleteNoteInSettings({ title, jwt }) {

    const router = useRouter()

    const [ showConfirmDialog, setShowConfirmDialog ] = useState(false)

    const handleDelete = async () => {
        setShowConfirmDialog(false)

        if(!jwt) return
        
        const requestOptions = {
            method: 'DELETE',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt}
        }
        const response = await fetch(`http://localhost:8080/api/v1/notes/principal/note/${encodeURI(title)}`, requestOptions)
        console.log(response.status)

        if(response.status == 200) {
            router.push('/notes')
        } else {

        }

    }

    return (
        <div style={{margin: '25px 0', width: '100%', display: 'flex', justifyContent: 'space-around'}}>
            <Button
                variant='contained'
                color='secondary'
                onClick={() => setShowConfirmDialog(true)}
                style={{margin: '0 auto'}}
            >
                Delete Note
            </Button>

            <YesNoDialog 
                title={`Are you sure you want to delete ${title}?`}
                text="This action can be undo'd for two weeks. After that, all history of the note will be deleted. Are you sure you wish to delete this note?"
                onClose={() => setShowConfirmDialog(false)}
                open={showConfirmDialog}
                onResponse={val => val ? handleDelete() : setShowConfirmDialog(false)}
            />
        </div>
        
    )

}
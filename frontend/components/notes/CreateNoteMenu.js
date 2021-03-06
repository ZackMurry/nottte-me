import React, { useState } from 'react'
import { Dialog, DialogTitle, DialogContent, DialogContentText, DialogActions, TextField, Button } from '@material-ui/core'
import { useRouter } from 'next/router'

//todo title validation
export default function CreateNoteMenu({ jwt, open, onClose }) {
    const [ title, setTitle ] = useState('')

    const router = useRouter()

    const createNote = async () => {
        if (!jwt) return
        console.log('sending')

        //body.body is just a default editor state.
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt },
            body: JSON.stringify({
                title
            })
        }

        const response = await fetch('http://localhost:8080/api/v1/notes/create', requestOptions)
        const { status } = response
        if (status === 200) {
            router.push('/n/' + encodeURI(title)) //encoding so that it'll work in a URL
        }
        //else tell user
    }

    const handleKeyDown = e => {
        if (e.key === 'Enter') {
            console.log(title)
            e.preventDefault() //prevents enter key from being in title
            createNote()
        }
    }

    const handleClose = () => {
        setTitle('')
        onClose()
    }

    return (
        open
        && (
            <Dialog
                onClose={handleClose}
                open={open}
                onContextMenu={e => e.stopPropagation()}
                maxWidth='sm'
                fullWidth
            >
                <DialogTitle>
                    Create new note
                </DialogTitle>
                <DialogContent style={{ paddingBottom: 0 }}>
                    <DialogContentText>
                        Please enter a name for your note
                    </DialogContentText>
                    <TextField
                        id='new-note'
                        fullWidth
                        label='Title...'
                        value={title}
                        onChange={e => setTitle(e.target.value)}
                        placeholder='title'
                        onKeyDown={handleKeyDown}
                        style={{ marginBottom: 0, paddingBottom: 0 }}
                    />
                </DialogContent>
                <DialogActions style={{ padding: '2.5vw', display: 'flex', justifyContent: 'flex-end', paddingTop: '3vh' }}>
                    <Button onClick={handleClose}>
                        Cancel
                    </Button>
                    <Button
                        onClick={createNote}
                        variant='contained'
                        color='secondary'
                    >
                        Confirm
                    </Button>
                </DialogActions>
            </Dialog>
        )

    )
}

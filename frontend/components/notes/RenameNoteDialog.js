import {
    Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, TextField, Typography
} from '@material-ui/core'
import { useEffect, useState } from 'react'
import validateTitle from '../utils/ValidateTitle'

export default function RenameNoteDialog({
    open, onClose, title, onRename, jwt, onError
}) {
    const [ editedTitle, setEditedTitle ] = useState(title || 'err')
    const [ invalidTitleText, setInvalidTitleText ] = useState('')

    const handleClose = () => {
        setEditedTitle(title)
        setInvalidTitleText('')
        onClose()
    }

    const handleRename = async () => {
        const validateResponse = validateTitle(editedTitle)
        if (validateResponse) {
            setInvalidTitleText(validateResponse)
            return
        }

        onClose()
        if (title === editedTitle) return

        const requestOptions = {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt }
        }

        const response = await fetch(
            `http://localhost:8080/api/v1/notes/principal/note/${title}/rename/${editedTitle}`,
            requestOptions
        ).catch(() => onError('Error renaming note. Please try again later.'))

        if (!response) {
            return
        }
        if (response.status === 200) {
            onRename(editedTitle)
        } else if (response.status < 400) {
            console.log(response.status)
        } else {
            onError('Error renaming note. Error code: ' + response + '.')
            console.log(response.status)
        }
    }

    useEffect(() => {
        setEditedTitle(title)
    }, [ title ])

    return (
        <Dialog
            onClose={handleClose}
            open={open}
            onContextMenu={e => e.stopPropagation()}
        >
            <DialogTitle>
                Rename note
            </DialogTitle>
            <DialogContent>
                <DialogContentText>
                    Enter a new name for your note
                    <Typography color='error'>
                        {invalidTitleText}
                    </Typography>
                </DialogContentText>
                <TextField
                    margin='dense'
                    id='rename'
                    label='New title'
                    fullWidth
                    value={editedTitle}
                    onChange={e => setEditedTitle(e.target.value)}
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={handleClose}>
                    Cancel
                </Button>
                <Button onClick={handleRename} variant='contained' color='secondary'>
                    Confirm
                </Button>
            </DialogActions>
        </Dialog>
    )
}

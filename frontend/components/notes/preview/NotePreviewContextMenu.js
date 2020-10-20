import {
    Button, ClickAwayListener, Grow, MenuItem, Paper, Typography
} from '@material-ui/core'
import DeleteIcon from '@material-ui/icons/Delete'
import EditIcon from '@material-ui/icons/Edit'
import FileCopyIcon from '@material-ui/icons/FileCopy'

export const NOTE_PREVIEW_CONTEXT_TOGGLE_TIMEOUT = 100

const UNKNOWN_ERROR = 'There was an unknown error. Please try again soon.'

//todo maybe have one of these for the whole page
export default function NotePreviewContextMenu({
    show, onClose, pos, onDelete, onRename, isShared, title, author, jwt, onDuplicate, setSnackbarText, setShowSnackbar
}) {
    const handleContextMenu = e => {
        e.persist()
        e.preventDefault()
        e.stopPropagation()
    }

    const handleDelete = () => {
        onClose()
        onDelete()
    }

    const handleRename = () => {
        onClose()
        onRename()
    }

    const handleDuplicate = async () => {
        onClose()

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt }
        }

        if (isShared) {
            const response = await fetch(
                `http://localhost:8080/api/v1/shares/principal/note/${author}/${title}/duplicate`,
                requestOptions
            ).catch(() => console.error('error duplicating note'))
            if (response.status >= 400) {
                if (response.status === 404) {
                    setSnackbarText('Cannot find note by ' + author + ' with title ' + title + '.')
                }
            } else {
                const duplicatedName = await response.text()
                setSnackbarText('Successfully duplicated note; new title: ' + duplicatedName)
                onDuplicate(duplicatedName)
            }
            setShowSnackbar(true)
        } else {
            const response = await fetch(
                `http://localhost:8080/api/v1/notes/principal/note/${title}/duplicate`,
                requestOptions
            ).catch(() => console.error('error duplicating note'))
            if (response.status >= 400) {
                setSnackbarText(UNKNOWN_ERROR)
            } else {
                const duplicatedName = await response.text()
                setSnackbarText('Successfully duplicated note; new title: ' + duplicatedName)
                onDuplicate(duplicatedName)
            }
            setShowSnackbar(true)
        }
    }

    return (
        <>
            <ClickAwayListener onClickAway={onClose}>
                <div
                    style={{
                        position: 'fixed',
                        top: show ? (pos?.y || -500) : -500,
                        left: show ? (pos?.x || -100) : -100,
                        zIndex: 20
                    }}
                    onContextMenu={handleContextMenu}
                >
                    <Grow in={show} timeout={NOTE_PREVIEW_CONTEXT_TOGGLE_TIMEOUT}>
                        <Paper elevation={3} style={{ borderRadius: 7, padding: '5px 0' }}>
                            <MenuItem
                                style={{ padding: 0, minHeight: 36 }}
                                className='context-button'
                            >
                                <Button
                                    onClick={handleDelete}
                                    variant='contained'
                                    color='primary'
                                    startIcon={<DeleteIcon />}
                                    style={{
                                        margin: 0,
                                        borderRadius: 0,
                                        justifyContent: 'flex-start',
                                        textTransform: 'none'
                                    }}
                                    disableFocusRipple
                                    fullWidth
                                >
                                    <Typography style={{ padding: '0 5px' }}>
                                        Delete
                                    </Typography>
                                </Button>
                            </MenuItem>
                            <MenuItem
                                style={{ padding: 0, minHeight: 36 }}
                                className='context-button'
                            >
                                <Button
                                    onClick={handleRename}
                                    variant='contained'
                                    color='primary'
                                    startIcon={<EditIcon />}
                                    style={{
                                        margin: 0,
                                        borderRadius: 0,
                                        justifyContent: 'flex-start',
                                        textTransform: 'none'
                                    }}
                                    disableFocusRipple
                                    fullWidth
                                >
                                    <Typography style={{ padding: '0 5px' }}>
                                        Rename
                                    </Typography>
                                </Button>
                            </MenuItem>
                            <MenuItem
                                style={{ padding: 0, minHeight: 36 }}
                                className='context-button'
                            >
                                <Button
                                    onClick={handleDuplicate}
                                    variant='contained'
                                    color='primary'
                                    startIcon={<FileCopyIcon />}
                                    style={{
                                        margin: 0,
                                        borderRadius: 0,
                                        justifyContent: 'flex-start',
                                        textTransform: 'none'
                                    }}
                                    disableFocusRipple
                                    fullWidth
                                >
                                    <Typography style={{ padding: '0 5px' }}>
                                        Duplicate
                                    </Typography>
                                </Button>
                            </MenuItem>
                        </Paper>
                    </Grow>
                </div>
            </ClickAwayListener>
        </>
    )
}

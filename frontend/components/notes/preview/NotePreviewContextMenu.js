import {
    Button, ClickAwayListener, Grow, MenuItem, Paper, Typography
} from '@material-ui/core'
import DeleteIcon from '@material-ui/icons/Delete'

export const NOTE_PREVIEW_CONTEXT_TOGGLE_TIMEOUT = 100

//todo maybe have one of these for the whole page
export default function NotePreviewContextMenu({
    show, onClose, pos, onDelete
}) {
    const handleContextMenu = e => {
        e.persist()
        e.preventDefault()
        e.stopPropagation()
    }

    return (
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
                            style={{ padding: 0 }}
                            className='context-button'
                        >
                            <Button
                                onClick={onDelete}
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
                        {/* todo add more */}
                    </Paper>
                </Grow>
            </div>
        </ClickAwayListener>
    )
}

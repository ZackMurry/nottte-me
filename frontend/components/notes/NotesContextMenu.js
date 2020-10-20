import {
    Button, ClickAwayListener, Grow, MenuItem, Paper, Typography
} from '@material-ui/core'
import SubjectIcon from '@material-ui/icons/Subject'
import CreateNewFolderIcon from '@material-ui/icons/CreateNewFolder'

//todo add: share, star, add to folder, create sharable link, download as pdf, ...
export default function NotesContextMenu({
    onCreateNote, show, pos, setShow, setPos
}) {
    const handleCreateNote = () => {
        setShow(false)
        onCreateNote()
    }

    const handleCreateFolder = () => {

    }

    const handleClose = () => {
        setShow(false)
        setTimeout(() => setPos({ x: -100, y: -500 }), 100)
    }

    return (
        <ClickAwayListener onClickAway={handleClose}>
            <div
                style={{
                    position: 'fixed',
                    top: pos.y,
                    left: pos.x,
                    zIndex: 20
                }}
                onContextMenu={e => e.stopPropagation()}
            >
                <Grow in={show} timeout={100}>
                    <Paper elevation={3} style={{ borderRadius: 7, padding: '5px 0' }}>
                        <MenuItem
                            onClick={handleCreateNote}
                            style={{ padding: 0, minHeight: 36 }}
                            className='context-button'
                        >
                            <Button
                                variant='contained'
                                color='primary'
                                startIcon={<SubjectIcon />}
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
                                    New note
                                </Typography>
                            </Button>
                        </MenuItem>
                        <MenuItem
                            onClick={handleCreateFolder}
                            style={{ padding: 0, minHeight: 36 }}
                            className='context-button'
                        >
                            <Button
                                variant='contained'
                                color='primary'
                                startIcon={<CreateNewFolderIcon />}
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
                                    New folder
                                </Typography>
                            </Button>
                        </MenuItem>
                    </Paper>
                </Grow>
            </div>
        </ClickAwayListener>
    )
}

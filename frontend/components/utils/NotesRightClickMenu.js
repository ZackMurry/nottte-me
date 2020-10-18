import { Button, ClickAwayListener, Grow, MenuItem, Paper, Typography } from '@material-ui/core'
import { useEffect, useState } from 'react'
import SubjectIcon from '@material-ui/icons/Subject'
import CreateNewFolderIcon from '@material-ui/icons/CreateNewFolder'

export default function NotesRightClickMenu({
    onCreateNote
}) {
    const [ showContext, setShowContext ] = useState(false)
    const [ contextPos, setContextPos ] = useState({ x: 0, y: 0 })

    useEffect(() => {
        const handleRightClick = e => {
            e.preventDefault()
            const updateContext = () => {
                setShowContext(true)
                setContextPos({ x: e.clientX, y: e.clientY })
            }
            if (showContext) {
                setShowContext(false)
                setTimeout(updateContext, 150)
            } else {
                updateContext()
            }
            console.log(e)
        }
        document.addEventListener('contextmenu', handleRightClick)
        return () => {
            document.removeEventListener('contextmenu', handleRightClick)
        }
    }, [ showContext, setShowContext, setContextPos ])

    const handleCreateNote = () => {
        setShowContext(false)
        onCreateNote()
    }

    const handleCreateFolder = () => {

    }

    return (
        <ClickAwayListener onClickAway={() => setShowContext(false)}>
            <div
                style={{
                    position: 'fixed',
                    top: contextPos.y,
                    left: contextPos.x,
                    zIndex: 20
                }}
            >
                <Grow in={showContext}>
                    <Paper color='secondary' elevation={3} style={{ borderRadius: 7, padding: '5px 0' }}>
                        <MenuItem
                            onClick={handleCreateNote}
                            style={{ padding: 0 }}
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
                            style={{ padding: 0 }}
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

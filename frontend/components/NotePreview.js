import React, { useEffect, useState } from 'react'
import { Paper, Typography, Card, CardContent, CardActions, IconButton, Popper, Grow, ClickAwayListener, Button, TextField } from '@material-ui/core'
import { useRouter } from 'next/router'
import { EditorState, convertFromRaw } from 'draft-js'
import MoreVertIcon from '@material-ui/icons/MoreVert';
import DeleteIcon from '@material-ui/icons/Delete';
import EditIcon from '@material-ui/icons/Edit';
import YesNoDialog from './YesNoDialog';
import DoneIcon from '@material-ui/icons/Done';

export default function NotePreview({ name, editorState, jwt, onNoteRename }) {

    const [ rawText, setRawText] = useState('')
    const [ showingMore, setShowingMore ] = useState(false)
    const [ anchorElement, setAnchorElement ] = useState(null)

    const [ showDeleteDialog, setShowDeleteDialog ] = useState(false)
    const [ editingName, setEditingName ] = useState(false)
    const [ editedName, setEditedName ] = useState(name)

    const router = useRouter()

    useEffect(() => {
        if(editorState) {
            
            let raw = convertFromRaw(JSON.parse(editorState))
            let objEditorState = EditorState.createWithContent(raw)
            let currentContent = objEditorState.getCurrentContent()
            let plainText = currentContent.getPlainText('') + ''
            setRawText(plainText)
        }
    }, [ editorState ])

    useEffect(() => {
        setEditedName(name)
    }, [ name ])

    const goToNotePage = () => {
        router.push('/n/' + encodeURI(name))
    }

    const onShowMore = e => {
        e.stopPropagation()
        setShowingMore(!showingMore)
        setAnchorElement(e.currentTarget)
    }
    
    const handleDelete = async () => {
        console.log('deleting...')
        setShowDeleteDialog(false)

        const requestOptions = {
            method: 'DELETE',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt}
        }
        const response = await fetch(`http://localhost:8080/api/v1/notes/principal/note/${encodeURI(name)}`, requestOptions)
        console.log(response.status)

        router.reload()
    }

    const handleDoneRenaming = async e => {
        e.stopPropagation()
        setEditingName(false)

        if(name == editedName) return

        const requestOptions = {
            method: 'PATCH',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt}
        }

        const response = await fetch(`http://localhost:8080/api/v1/notes/principal/note/${name}/rename/${editedName}`, requestOptions)

        if(response.status == 200) {
            onNoteRename(editedName)
        } else {
            console.log(response.status)
        }

    }
    
    return (
        <React.Fragment>
            <Popper open={showingMore} anchorEl={anchorElement} placement={'right'}>
                <ClickAwayListener onClickAway={() => setShowingMore(false)}>
                    <Grow timeout={350} in={showingMore}>
                        <Paper elevation={5} style={{maxWidth: '12.5vw'}}>
                            <Button
                                color='primary'
                                startIcon={<DeleteIcon color='secondary' fontSize='large' />}
                                style={{textTransform: 'none', width: '100%', maxWidth: '12.5vw'}}
                                onClick={() => {setShowDeleteDialog(true); setShowingMore(false)}}
                            >
                                <Typography color='secondary'>
                                    Delete note
                                </Typography>
                            </Button>
                            <Button
                                color='primary'
                                startIcon={<EditIcon color='secondary' fontSize='large'/>}
                                style={{textTransform: 'none', width: '100%', maxWidth: '12.5vw'}}
                                onClick={() => {setEditingName(true); setShowingMore(false)}}
                            >
                                <Typography color='secondary'> 
                                    Rename note
                                </Typography>
                            </Button>
                        </Paper>
                    </Grow>
                </ClickAwayListener>
            </Popper>
            <div style={{margin: 0, cursor: 'pointer'}} onClick={() => goToNotePage()}>
                <Card>
                    <CardContent>
                        <div style={{backgroundColor: '#2d323e', width: '100%', height: '25vh', borderRadius: 10, margin: 0, padding: 0, display: 'flex'}}>
                            <Paper elevation={0} style={{width: '60%', marginLeft: 'auto', marginRight: 'auto', marginBottom: 0, height: '60%', verticalAlign: 'bottom', alignSelf: 'flex-end', borderRadius: '10px 10px 0 0', overflow: 'hidden'}}>
                                <Typography style={{margin: 10, width: '100%', height: '92.5%', overflowY: 'scroll', paddingRight: 3, boxSizing: 'content-box'}}>
                                    { rawText }
                                </Typography>
                            </Paper>
                        </div>
                    </CardContent>
                    <CardActions>
                        {
                            editingName
                            ?
                            <div style={{display: 'flex', justifyContent: 'space-between', width: '100%'}} onClick={e => e.stopPropagation()}>
                                <TextField
                                    value={editedName}
                                    onChange={e => setEditedName(e.target.value)}
                                    onClick={e => e.stopPropagation()}
                                    style={{marginLeft: 10, width: '85%', marginRight: 10}}
                                    InputProps={{style: {fontSize: 28}}}
                                />
                                <IconButton onClick={handleDoneRenaming}>
                                    <DoneIcon color='secondary' />
                                </IconButton>
                            </div>
                            :
                            <div style={{display: 'flex', justifyContent: 'space-between', width: '100%'}}>
                                <Typography variant='h4' style={{marginLeft: 10}}>{ name }</Typography>
                                <IconButton onClick={onShowMore}>
                                    <MoreVertIcon color='secondary' />
                                </IconButton>
                            </div>
                        }
                        
                    </CardActions>
                    
                </Card>
                
            </div>

            {/* confirm delete dialog */}
            <YesNoDialog 
                title='Are you sure you want to delete this note?' 
                text="This action can be undo'd for two weeks. After that, all history of the note will be deleted. Are you sure you wish to delete this note?"
                onClose={() => setShowDeleteDialog(false)}
                open={showDeleteDialog}
                onResponse={val => val ? handleDelete() : setShowDeleteDialog(false)}
            />
        </React.Fragment>
        
        
    )

}

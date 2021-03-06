import React, { useEffect, useState } from 'react'
import {
    Paper, Typography, Card, CardContent, CardActions
} from '@material-ui/core'
import { useRouter } from 'next/router'
import { EditorState, convertFromRaw } from 'draft-js'
import PeopleIcon from '@material-ui/icons/People'
import PlainSnackbar from '../../utils/PlainSnackbar'
import YesNoDialog from '../../utils/YesNoDialog'
import NotePreviewContextMenu, { NOTE_PREVIEW_CONTEXT_TOGGLE_TIMEOUT } from './NotePreviewContextMenu'
import RenameNoteDialog from '../RenameNoteDialog'

//todo maybe force renaming from context instead of three dots menu
export default function NotePreview({
    name,
    editorState,
    jwt,
    onNoteRename,
    shared,
    author,
    contextOpen,
    onContextOpen,
    onContextClose,
    contextPos,
    updateContextPos,
    onDelete,
    onDuplicate
}) {
    const [ rawText, setRawText] = useState('')

    const [ showDeleteDialog, setShowDeleteDialog ] = useState(false)
    const [ showRenameSnackbar, setShowRenameSnackbar ] = useState(false)

    const [ showSnackbar, setShowSnackbar] = useState(false)
    const [ snackbarText, setSnackbarText ] = useState('There was an unknown error. Please try again soon.')

    const [ renaming, setRenaming ] = useState(false)

    const router = useRouter()

    useEffect(() => {
        if (editorState) {
            const raw = convertFromRaw(JSON.parse(editorState))
            const objEditorState = EditorState.createWithContent(raw)
            const currentContent = objEditorState.getCurrentContent()
            const plainText = currentContent.getPlainText('') + ''
            setRawText(plainText)
        }
    }, [ editorState ])

    useEffect(() => {
        const handleRightClick = e => {
            e.preventDefault()
            e.stopPropagation()
        }

        document.addEventListener('contextmenu', handleRightClick)
        return () => {
            document.removeEventListener('contextmenu', handleRightClick)
        }
    })

    const goToNotePage = () => {
        router.push(shared ? (`/u/${encodeURI(author)}/${encodeURI(name)}`) : ('/n/' + encodeURI(name)))
    }

    const handleDelete = async () => {
        console.log('deleting...')
        setShowDeleteDialog(false)

        const requestOptions = {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt }
        }
        const response = await fetch(`http://localhost:8080/api/v1/notes/principal/note/${encodeURI(name)}`, requestOptions)
        console.log(response.status)

        onDelete()
    }

    const handleContextMenu = e => {
        e.persist()
        e.preventDefault()
        e.stopPropagation()

        if (renaming) return

        const updateContext = () => {
            onContextOpen()
            setTimeout(() => updateContextPos({ x: e.clientX, y: e.clientY }), NOTE_PREVIEW_CONTEXT_TOGGLE_TIMEOUT)
        }

        if (contextOpen) {
            onContextClose()
            setTimeout(updateContext, NOTE_PREVIEW_CONTEXT_TOGGLE_TIMEOUT)
        } else {
            updateContext()
        }
    }

    const handleContextClose = () => {
        //updating contextPos so that the user can still click on everything
        onContextClose()
        setTimeout(() => updateContextPos({ x: -100, y: -500 }), NOTE_PREVIEW_CONTEXT_TOGGLE_TIMEOUT)
    }

    return (
        <>
            <div style={{ margin: 0, cursor: 'pointer' }} onClick={goToNotePage} onContextMenu={handleContextMenu}>
                <Card>
                    <CardContent>
                        <div
                            style={{
                                backgroundColor: '#2d323e',
                                width: '100%',
                                height: '25vh',
                                borderRadius: 10,
                                margin: 0,
                                padding: 0,
                                display: 'flex'
                            }}
                        >
                            {
                                shared && (
                                    <div style={{
                                        position: 'relative', top: 0, left: '87.5%', width: 0
                                    }}
                                    >
                                        <div style={{
                                            position: 'absolute', left: 0, top: 17.5, zIndex: 5, display: 'flex', justifyContent: 'flex-end'
                                        }}
                                        >
                                            <PeopleIcon color='primary' />
                                        </div>
                                    </div>
                                )
                            }
                            <Paper
                                elevation={0}
                                style={{
                                    width: '60%',
                                    marginLeft: 'auto',
                                    marginRight: 'auto',
                                    marginBottom: 0,
                                    height: '60%',
                                    verticalAlign: 'bottom',
                                    alignSelf: 'flex-end',
                                    borderRadius: '10px 10px 0 0',
                                    overflow: 'hidden'
                                }}
                            >
                                <Typography
                                    style={{
                                        margin: 10,
                                        width: '100%',
                                        height: '92.5%',
                                        overflowY: 'scroll',
                                        paddingRight: 3,
                                        boxSizing: 'content-box',
                                        overflowX: 'hidden'
                                    }}
                                >
                                    { rawText }
                                </Typography>
                            </Paper>
                        </div>
                    </CardContent>
                    <CardActions>
                        <div style={{ display: 'flex', justifyContent: 'space-between', width: '100%' }}>
                            <Typography variant='h4' style={{ marginLeft: 10, paddingBottom: 7 }}>
                                { name }
                            </Typography>
                        </div>
                    </CardActions>

                </Card>

            </div>

            {/* confirm delete dialog */}
            <YesNoDialog
                title='Are you sure you want to delete this note?'
                // eslint-disable-next-line max-len
                text="This action can be undo'd for two weeks. After that, all history of the note will be deleted. Are you sure you wish to delete this note?"
                onClose={() => setShowDeleteDialog(false)}
                open={showDeleteDialog}
                onResponse={val => (val ? handleDelete() : setShowDeleteDialog(false))}
            />

            <RenameNoteDialog
                open={renaming}
                onClose={() => setRenaming(false)}
                title={name}
                onRename={onNoteRename}
                jwt={jwt}
            />

            <PlainSnackbar
                message='Error renaming note. Please try again.'
                duration={3000}
                value={showRenameSnackbar}
                onClose={() => setShowRenameSnackbar(false)}
            />

            <PlainSnackbar
                message={snackbarText}
                duration={3000}
                value={showSnackbar}
                onClose={() => setShowSnackbar(false)}
            />

            {
                contextOpen && (
                    <NotePreviewContextMenu
                        show={contextOpen}
                        onClose={() => handleContextClose()}
                        pos={contextPos}
                        onDelete={() => setShowDeleteDialog(true)}
                        onRename={() => setRenaming(true)}
                        onDuplicate={onDuplicate}
                        isShared={shared}
                        title={name}
                        author={author}
                        jwt={jwt}
                        setShowSnackbar={val => setShowSnackbar(val)}
                        setSnackbarText={val => setSnackbarText(val)}
                    />
                )
            }
        </>

    )
}

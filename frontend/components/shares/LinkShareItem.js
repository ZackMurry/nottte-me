import {
    Grid, IconButton, makeStyles, MenuItem, Select
} from '@material-ui/core'
import DoneIcon from '@material-ui/icons/Done'
import EditIcon from '@material-ui/icons/Edit'
import { useEffect, useState } from 'react'
import PlainSnackbar from '../utils/PlainSnackbar'
import PlainTooltip from '../utils/PlainTooltip'

export default function LinkShareItem({
    id, authority, status, jwt, onUpdate
}) {
    const [ editing, setEditing ] = useState(false)
    const [ editedAuthority, setEditedAuthority ] = useState(authority)
    const [ editedStatus, setEditedStatus ] = useState(status)
    const [ showCopiedSnackbar, setShowCopiedSnackbar ] = useState(false)

    useEffect(() => {
        setEditedAuthority(authority)
        setEditedStatus(status)
    }, [ authority, status ])

    const useTooltipStyles = makeStyles(() => ({
        tooltip: {
            marginTop: -10
        }
    }))

    const tooltipClass = useTooltipStyles()

    const handleUpdateLinkShare = async () => {
        if (editedAuthority === authority && editedStatus === status) {
            return
        }

        const requestOptions = {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt },
            body: JSON.stringify({
                authority: editedAuthority,
                status: editedStatus
            })
        }

        const response = await fetch(`http://localhost:8080/api/v1/shares/link/principal/${id}`, requestOptions)
        if (response.status >= 400) {
            console.log(response.status)
            return
        }
        onUpdate(editedAuthority, editedStatus)
    }

    const handleClickEdit = () => {
        if (editing) {
            handleUpdateLinkShare()
            setEditing(false)
        } else {
            setEditing(true)
        }
    }

    const copyLinkToClipboard = async () => {
        navigator.clipboard.writeText(`localhost:3000/l/${id}`)
        setShowCopiedSnackbar(true)
    }

    return (
        <>
            <Grid container spacing={3} item xs={12}>
                {
                    editing
                        ? (
                            <>
                            <Grid item xs={12} md={6}>
                                {id}
                            </Grid>
                            <Grid item xs={3} md={2}>
                                <Select
                                    labelId='select-authority-label'
                                    id='select-authority'
                                    value={editedAuthority}
                                    onChange={e => setEditedAuthority(e.target.value)}
                                >
                                    <MenuItem value='VIEW'>VIEW</MenuItem>
                                    <MenuItem value='EDIT'>EDIT</MenuItem>
                                </Select>
                            </Grid>
                            <Grid item xs={3}>
                                <Select
                                    labelId='select-status-label'
                                    id='select-status'
                                    value={editedStatus}
                                    onChange={e => setEditedStatus(e.target.value)}
                                >
                                    <MenuItem value='ACTIVE'>ACTIVE</MenuItem>
                                    <MenuItem value='DISABLED'>DISABLED</MenuItem>
                                </Select>
                            </Grid>
                            </>
                        )
                        : (
                            <>
                            <PlainTooltip title='copy link' classes={tooltipClass}>
                                <Grid
                                    item
                                    xs={12}
                                    md={6}
                                    onClick={copyLinkToClipboard}
                                    style={{
                                        cursor: 'pointer', lineBreak: 'anywhere', fontSize: 14, paddingRight: 0
                                    }}
                                >
                                    {id}
                                </Grid>
                            </PlainTooltip>
                            <Grid item xs={3} md={2}>
                                {authority}
                            </Grid>
                            <Grid item xs={3}>
                                {status}
                            </Grid>
                            </>
                        )
                }

                <Grid item xs={3} md={1}>
                    <IconButton onClick={handleClickEdit} style={{ width: 18, height: 18 }}>
                        {
                            editing
                                ? <DoneIcon fontSize='small' />
                                : <EditIcon fontSize='small' />
                        }
                    </IconButton>
                </Grid>
            </Grid>

            <PlainSnackbar
                message='link copied to clipboard'
                value={showCopiedSnackbar}
                onClose={() => setShowCopiedSnackbar(false)}
            />
        </>
    )
}

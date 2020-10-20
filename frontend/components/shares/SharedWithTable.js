import {
    Checkbox, Grid, IconButton, Typography
} from '@material-ui/core'
import React, { useEffect, useState } from 'react'
import RemoveIcon from '@material-ui/icons/Remove'
import PlainTooltip from '../utils/PlainTooltip'

export default function SharedWithTable({
    jwt, title
}) {
    const [ sharedWith, setSharedWith ] = useState([])

    const getData = async () => {
        //getting who this note is shared with
        if (!jwt) return

        const requestOptions = {
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt }
        }

        const response = await fetch(`http://localhost:8080/api/v1/shares/principal/note/${encodeURI(title)}/shares`, requestOptions)
        if (response.status !== 200) {
            console.log(response.status)
            return
        }
        const text = await response.text()
        console.log(text)
        setSharedWith(JSON.parse(text))
    }

    useEffect(() => {
        if (title) {
            getData()
        }
    }, [ title ])

    const removeShareFromArray = removedUsername => {
        const updatedSharedWith = sharedWith.slice()
        let index = -1
        for (let i = 0; i < updatedSharedWith.length; i++) {
            const noteShare = updatedSharedWith[i]
            if (noteShare.sharedUsername === removedUsername) {
                index = i
                break
            }
        }
        if (index === -1) {
            console.log('index should not be -1.')
            return
        }
        updatedSharedWith.splice(index, 1)
        setSharedWith(updatedSharedWith)
    }

    const unshareWithUser = async username => {
        if (!jwt) return
        const requestOptions = {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt }
        }

        const response = await fetch(`http://localhost:8080/api/v1/shares/principal/share/${title}/${username}`, requestOptions)

        if (response.status === 200) {
            removeShareFromArray(username)
        }
    }

    const setUserCanShare = async (username, newValue) => {
        if (!jwt || !username) return
        const requestOptions = {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt },
            body: newValue
        }
        const response = await fetch(`http://localhost:8080/api/v1/shares/principal/note/${title}/shares/${username}/can-share`, requestOptions)
        if (response.status >= 400) {
            console.error('error setting can share')
            return
        }
        const updatedSharedWith = sharedWith.slice()
        for (let i = 0; i < updatedSharedWith.length; i++) {
            const noteShare = updatedSharedWith[i]
            if (noteShare.sharedUsername === username) {
                noteShare.canShare = newValue
                setSharedWith(updatedSharedWith)
                console.log('success: ' + noteShare.canShare)
                return
            }
        }
        console.error('cannot find noteShare with name ' + username)
    }

    return (
        <div>
            <Grid container spacing={3}>
                {
                    sharedWith.length > 0 && (
                        <>
                            <Grid item xs={5}>
                                <Typography style={{ fontWeight: 700 }}>
                                    Username
                                </Typography>
                            </Grid>
                            <Grid item xs={3}>
                                <Typography style={{ fontWeight: 700 }}>
                                    Permissions
                                </Typography>
                            </Grid>
                            <Grid item xs={2}>
                                <Typography style={{ fontWeight: 700 }}>
                                    Actions
                                </Typography>
                            </Grid>
                            <Grid item xs={2}>
                                <Typography style={{ fontWeight: 700 }}>
                                    Can share
                                </Typography>
                            </Grid>
                            {
                                sharedWith.map(noteShare => (
                                    <React.Fragment key={noteShare.sharedUsername}>
                                        <Grid item xs={5}>
                                            <Typography style={{ fontSize: 18 }}>
                                                {noteShare.sharedUsername}
                                            </Typography>
                                        </Grid>
                                        <Grid item xs={3}>
                                            <Typography style={{ fontSize: 18 }}>
                                                {/* todo add editing and stuff (maybe commenting too) */}
                                                Read
                                            </Typography>
                                        </Grid>
                                        <Grid item xs={2}>
                                            <PlainTooltip title='Unshare'>
                                                <IconButton
                                                    onClick={() => unshareWithUser(noteShare.sharedUsername)}
                                                    style={{ width: 18, height: 18 }}
                                                >
                                                    <RemoveIcon fontSize='small' />
                                                </IconButton>
                                            </PlainTooltip>
                                        </Grid>
                                        <Grid item xs={2}>
                                            <Checkbox
                                                checked={noteShare.canShare}
                                                onClick={() => setUserCanShare(noteShare.sharedUsername, !noteShare.canShare)}
                                                size='small'
                                                style={{ padding: 0 }}
                                                disableFocusRipple
                                            />
                                        </Grid>
                                    </React.Fragment>
                                ))
                            }
                        </>
                    )
                }
                {
                    sharedWith.length === 0 && (
                        <Grid item xs={12}>
                            <Typography variant='h5' style={{ textAlign: 'center', marginTop: '3vh' }}>
                                This note isn't shared with anyone.
                                You should add some friends!
                            </Typography>
                        </Grid>
                    )
                }
            </Grid>
        </div>

    )
}

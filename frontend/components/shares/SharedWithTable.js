import { Grid, IconButton, Typography } from '@material-ui/core'
import React from 'react'
import PlainTooltip from '../utils/PlainTooltip'
import RemoveIcon from '@material-ui/icons/Remove';

export default function SharedWithTable({ sharedWith, jwt, title, onUnshare }) {

    const unshareWithUser = async (username) => {
        if(!jwt) return
        const requestOptions = {
            method: 'DELETE',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt}
        }

        const response = await fetch(`http://localhost:8080/api/v1/shares/principal/share/${title}/${username}`, requestOptions)

        if(response.status == 200) {
            onUnshare(username)
        }

    }

    return (
        <div>
            <Grid container spacing={3}>
                {
                    sharedWith.length > 0 && (
                        <>
                            <Grid item xs={6}>
                                <Typography variant='h6'>
                                    Username
                                </Typography>
                            </Grid>
                            <Grid item xs={3}>
                                <Typography variant='h6'>
                                    Permissions
                                </Typography>
                            </Grid>
                            <Grid item xs={3}>
                                <Typography variant='h6'>
                                    Actions
                                </Typography>
                            </Grid>
                            {
                                sharedWith.map(user => {
                                return (
                                    <React.Fragment key={user}>
                                        <Grid item xs={6}>
                                            <Typography style={{fontSize: 18}}>
                                                {user}
                                            </Typography>
                                        </Grid>
                                        <Grid item xs={3}>
                                            <Typography style={{fontSize: 18}}>
                                                {/* todo add editing and stuff (maybe commenting too) */}
                                                Read
                                            </Typography>
                                        </Grid>
                                        <Grid item xs={3}>
                                            <PlainTooltip title='Unshare'>
                                                <IconButton onClick={() => unshareWithUser(user)} style={{width: 18, height: 18}}>
                                                    <RemoveIcon fontSize='small'/>
                                                </IconButton>
                                            </PlainTooltip>
                                        </Grid>
                                    </React.Fragment>                            
                                )
                                })
                            }
                        </>
                    )
                }
                {
                    sharedWith.length == 0 && (
                        <Grid item xs={12} >
                            <Typography variant='h5' style={{textAlign: 'center', marginTop: '3vh'}}>
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
import React from 'react'
import { Grid, Typography } from '@material-ui/core'

//because you can't edit or delete these
export default function NottteShortcutDisplay({ name, button, description }) {
    return (
        <Grid container spacing={3}>
            <Grid item xs={12} lg={4}>
                <Typography>{ name }</Typography>
            </Grid>
            <Grid item xs={12} lg={2}>
                <Typography style={{ fontWeight: 500 }}>
                    CTRL +
                    { button }
                </Typography>
            </Grid>
            <Grid item xs={12} lg={6}>
                <Typography>{ description }</Typography>
            </Grid>
        </Grid>
    )
}

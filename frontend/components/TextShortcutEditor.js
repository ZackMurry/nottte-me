import React from 'react'
import { Typography, Grid } from '@material-ui/core'

//todo deleting and editing
export default function TextShortcutEditor({ name, button, text}) {


    return (
        <Grid container spacing={3}>
            <Grid item xs={12} lg={4}>
                <Typography>{ name }</Typography>
            </Grid>
            <Grid item xs={12} lg={2}>
                <Typography style={{fontWeight: 500}}>CTRL + { button }</Typography>
            </Grid>
            <Grid item xs={12} lg={6}>
                <Typography>{ text }</Typography>
            </Grid>
        </Grid>
    )

}
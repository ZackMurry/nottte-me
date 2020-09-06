import React from 'react'
import { Grid, Typography } from '@material-ui/core'

//todo deleting and editing
export default function StyleShortcutEditor({ name, button, attribute, value }) {


    return (
        <Grid container spacing={3}>
            <Grid item xs={12} lg={3}>
                <Typography>{ name }</Typography>
            </Grid>
            <Grid item xs={12} lg={2}>
                <Typography style={{fontWeight: 500}}>CTRL + { button }</Typography>
            </Grid>
            <Grid item xs={12} lg={4}>
                <Typography>{ attribute }</Typography>
            </Grid>
            <Grid item xs={12} lg={3}>
                <Typography>{ value }</Typography>
            </Grid>
        </Grid>
        
    )

}
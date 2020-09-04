import React from 'react'
import { Typography } from '@material-ui/core'

//todo design this page in figma because i have no idea how to style this
export default function KeyboardShortcutEditor({ title, key, text}) {


    return (
        <div style={{display: 'flex'}}>
            <Typography>{ title }</Typography>
            <Typography>{ key }</Typography>
            <Typography>{ text }</Typography>
        </div>
        
    )

}
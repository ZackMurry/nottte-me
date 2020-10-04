import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from '@material-ui/core'
import React from 'react'

export default function YesNoDialog({ open, onClose, title, text, onResponse }) {


    return (
        <Dialog 
            onClose={onClose} 
            open={open}
        >
            <DialogTitle color='secondary'>{title}</DialogTitle>
            <DialogContent>
                <DialogContentText>
                    {text}
                </DialogContentText>
            </DialogContent>
            <DialogActions style={{padding: '2.5%'}}>
                <Button onClick={() => onResponse(false)}>
                    No
                </Button>
                <Button onClick={() => onResponse(true)}>
                    Yes
                </Button>
            </DialogActions>
        </Dialog>
        
    )

}
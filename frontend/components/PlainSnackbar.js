import React from 'react'
import { Snackbar, IconButton, SnackbarContent } from "@material-ui/core";
import CloseIcon from '@material-ui/icons/Close';
import theme from './theme'

export default function PlainSnackbar ({ message, duration = 3000, value, onClose }) {

    return (
        <Snackbar 
            open={value}
            autoHideDuration={duration}
            onClose={onClose}
            anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'left'
            }}
            color='secondary'
            bodyStyle={{ backgroundColor: theme.palette.secondary.main}}
        >
            <SnackbarContent style={{backgroundColor: 'secondary'}} message={message} 
                action={
                    <React.Fragment>
                        <IconButton size='small' aria-label="close" color="inherit" onClick={onClose}>
                            <CloseIcon fontSize='small' />
                        </IconButton>
                    </React.Fragment>
                }   
            />
        </Snackbar>
    )

}

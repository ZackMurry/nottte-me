import { Button, TextField } from '@material-ui/core'
import { useState } from 'react'

export default function LandingPageSignUp() {

    return (
        <div style={{ marginTop: '1vh' }}>
            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                <Button
                    href='/signup'
                    variant='contained'
                >
                    Sign up
                </Button>
            </div>
            <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '2vh' }}>
                <Button
                    href='/login'
                    variant='outlined'
                    color='primary'
                >
                    Or login
                </Button>
            </div>
        </div>
    )
}

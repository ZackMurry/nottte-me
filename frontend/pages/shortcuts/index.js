import React from 'react'
import Navbar from '../../components/Navbar'
import { Paper, Typography } from '@material-ui/core'
import Head from 'next/head'
import KeyboardShortcutEditor from '../../components/KeyboardShortcutEditor'

export default function index() {


    return (
        <div>
            <Head>
                <title>Shortcuts</title>
            </Head>
            <div>
                <Navbar />
            </div>
            
            <Paper style={{margin: '0 auto', marginTop: '20vh', marginBottom: '20vh', width: '50%', minHeight: '120vh', borderRadius: 40, boxShadow: '5px 5px 10px black'}} >
                <Typography variant='h2' style={{textAlign: 'center', paddingTop: '2.5vh', marginBottom: '2.5vh'}}>
                    Shortcuts
                </Typography>
                <div>
                    <KeyboardShortcutEditor title='myKeyboardShortcut' key='G' text='my cool keyboard shortcut!' />
                </div>
            </Paper>

        </div>
        
    )

}
import React from 'react'
import Navbar from '../../../components/Navbar'
import { Paper, Typography } from '@material-ui/core'

//todo docs
export default function TextShortcut() {


    return (
        <div>
            <Navbar />
            <div style={{marginTop: '15vh'}}>
                <Paper style={{margin: '0 auto', marginTop: '20vh', width: '50%', height: '80vh', borderRadius: '40px 40px 0 0', boxShadow: '5px 5px 10px black'}} >
                    <Typography variant='h1' style={{padding: '3vh 0 3vh 0', textAlign: 'center'}}>
                        Text shortcuts
                    </Typography>
                    {/* content */}
                    <div style={{width: '80%', margin: '0 auto'}}>
                        <Typography style={{textAlign: 'center'}}>
                            Text shortcuts are an important part of the note-taking process.
                            On this page, you'll learn what text shortcuts are, how to create one, and how to use one.
                        </Typography>

                        {/* what are they */}
                        <div style={{margin: '5vh 0'}}>
                            <Typography variant='h2' style={{textAlign: 'center'}}>
                                What are text shortcuts?
                            </Typography>
                        </div>
                    </div>
                </Paper>
            </div>
        </div>
        
    )

}

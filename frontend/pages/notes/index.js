import React from 'react'
import Navbar from '../../components/Navbar'
import NotePreview from '../../components/NotePreview'
import { Grid, Paper, withStyles } from '@material-ui/core'

export default function Notes() {


    return (
        <div style={{marginTop: '10vh'}} >
            <Navbar />
            <div style={{backgroundColor: 'white', margin: '0 auto', width: '100vw', minHeight: '90vh'}}>
                
                {/* notes */}
                <div>
                    <Grid>
                        <Grid item xs={3}>
                            <NotePreview name='Note name' />
                        </Grid>
                    </Grid>
                </div>
                
                
            </div>
            
        </div>
        
    )

}
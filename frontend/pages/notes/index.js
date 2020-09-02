import React, { useState } from 'react'
import Navbar from '../../components/Navbar'
import NotePreview from '../../components/NotePreview'
import { Grid, Paper, withStyles, IconButton } from '@material-ui/core'
import CreateIcon from '@material-ui/icons/Create';
import CreateNoteMenu from '../../components/CreateNoteMenu';

//todo display user's actual notes
export default function Notes() {

    const [ menuOpen, setMenuOpen ] = useState(false)

    const handleCreateClick = () => {
        setMenuOpen(!menuOpen)
    }

    return (
        <div>
            <div style={{marginTop: '10vh'}} >
                <Navbar />
                <div style={{backgroundColor: 'white', margin: '0 auto', width: '100%', minHeight: '90vh'}}>
                    <div style={{}}>
                        
                    </div>
                    {/* notes */}
                    <div style={{margin: 0}}>
                        <Grid container spacing={3} style={{margin: 0, width: '100%'}}>
                            <Grid item xs={3}>
                                <NotePreview name='Note name' />
                            </Grid>
                            <Grid item xs={3}>
                                <NotePreview name='Another note name' />
                            </Grid>
                        </Grid>
                    </div>
                </div>
            </div>
            <div style={{position: 'absolute', bottom: '3vw', right: '3vw', width: '3.5vw', height: '3.5vw', backgroundColor: '#2d323e', borderRadius: '50%', display: 'flex', justifyContent: 'center', alignItems: 'center'}}>
                <IconButton onClick={handleCreateClick} style={{color: 'white'}}>
                    <CreateIcon fontSize='large' />
                </IconButton>
            </div>
            <div style={{position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)'}}>
                <CreateNoteMenu open={menuOpen} />
            </div>
        </div>
        
        
    )

}
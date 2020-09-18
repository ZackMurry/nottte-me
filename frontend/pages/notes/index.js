import React, { useState, useEffect } from 'react'
import Navbar from '../../components/Navbar'
import NotePreview from '../../components/NotePreview'
import { Grid, Paper, withStyles, IconButton, Typography, Fab } from '@material-ui/core'
import CreateIcon from '@material-ui/icons/Create';
import CreateNoteMenu from '../../components/CreateNoteMenu';
import Cookie from 'js-cookie'
import { useRouter } from 'next/router';

//todo display user's actual notes
export default function Notes() {

    const router = useRouter()

    const [ menuOpen, setMenuOpen ] = useState(false)
    const [ notes, setNotes ] = useState([])

    const jwt = Cookie.get('jwt')

    useEffect(() => { 
        if(!jwt) {
            router.push('/login') //todo redirect back to /notes after login
        } else {
            getNotesFromServer()
        }
    }, [ jwt ])

    const getNotesFromServer = async () => {
        const response = await fetch('http://localhost:8080/api/v1/notes/principal/notes', {
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt}
        })
        const text = await response.text()
        setNotes(JSON.parse(text))
    }

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
                            {
                                notes.map(note => (
                                    <Grid item xs={12} sm={6} md={4} lg={3} key={note.id}>
                                        <NotePreview name={note.title} editorState={note.body} jwt={jwt} />
                                    </Grid>
                                ))
                                
                            }

                            {
                                notes.length <= 0 && (
                                    <div style={{margin: '15% auto'}}>
                                        <Typography variant='h4'>
                                            You don't have any notes!
                                            Click the button in the bottom right to make some.
                                        </Typography>
                                    </div>
                                )    
                            }
                        </Grid>
                    </div>
                </div>
            </div>
            
            <div style={{position: 'absolute', bottom: '3vw', right: '3vw', width: '3.5vw', height: '3.5vw', backgroundColor: '#2d323e', borderRadius: '50%', display: 'flex', justifyContent: 'center', alignItems: 'center'}}>
                <Fab color='secondary' aria-label='new note' onClick={handleCreateClick} >
                    <CreateIcon fontSize='large' />
                </Fab>
            </div>
            <div style={{position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)'}}>
                <CreateNoteMenu open={menuOpen} onClose={handleCreateClick} />
            </div>
        </div>
        
        
    )

}

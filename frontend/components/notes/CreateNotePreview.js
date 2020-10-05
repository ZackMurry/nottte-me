import React, { useState } from 'react'
import { Paper, Typography, Card, CardContent, CardActions, IconButton, Button } from '@material-ui/core'
import { useRouter } from 'next/router'
import theme from '../theme';
import PlainTooltip from '../utils/PlainTooltip'
import CreateIcon from '@material-ui/icons/Create'

//todo don't let people rename or delete shared notes from here
export default function CreateNotePreview({ jwt, onCreate }) {


    const [ name, setName ] = useState('')
    const [ created, setCreated ] = useState(false)
    const [ creationError, setCreationError ] = useState('')

    const router = useRouter()

    const goToNotePage = () => {
        router.push('/n/' + encodeURI(name))
    }

    const handleCreate = e => {
        setCreated(!created)
        onCreate()
    }

    const validateTitle = (noteTitle = name) => {
        if(noteTitle.includes('%')) {
            setCreationError("Your note title cannot contain a percent sign.")
        } else if(noteTitle.length > 200) {
            setCreationError("Your note title cannot be longer than 200 characers.")
        } else if(noteTitle.includes('/')) {
            setCreationError("Your note cannot contain a slash, since URLs are encoded using slashes.")
        } else if(noteTitle.includes('\\')) {
            setCreationError("Your note title cannot contain a back slash (\\).")
        } else {
            return true
        }
        return false
    }

    const createNote = async () => {
        if(!validateTitle(name)) {
            return
        }

        console.log(jwt)
        if(!jwt) return
        console.log('sending')

        //body.body is just a default editor state. 
        const requestOptions = {
            method: 'POST',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt},
            body: JSON.stringify({
                title: name,
                body: JSON.stringify({
                    entityMap: {},
                    blocks: [
                      {
                        text: '',
                        key: 'nottte',
                        type: 'unstyled',
                        entityRanges: [],
                      },
                    ],
                  })
            })
        }

        const response = await fetch('http://localhost:8080/api/v1/notes/create/with-body', requestOptions)
        const status = response.status
        if(status == 200) {
            router.push('/n/' + encodeURI(name)) //encoding so that it'll work in a URL
        } else if(status == 412) {
            setCreationError("You can't create two notes with the same name.")
        } else if(status === 404) {
            setCreationError("Error contacting server.")
        } else {
            setCreationError("There was an unknown error. Try changing the name of your note.")
        }
    }

    const handleEnter = (event) => {
        if(event.key === 'Enter') {
            event.preventDefault()
            createNote()
        } 
    }
    
    return (
        <>
            <div style={{cursor: 'pointer'}} onClick={() => handleCreate()}>
                <Card>
                    <CardContent>
                        <div 
                            style={{
                                backgroundColor: '#2d323e',
                                width: '100%', 
                                height: '25vh', 
                                borderRadius: 10, 
                                margin: 0, 
                                padding: 0, 
                                display: 'flex', 
                                justifyContent: 'center',
                                alignItems: 'center'
                            }}
                        >
                            <IconButton style={{padding: '1vh 1vw', maxHeight: 96, maxWidth: 96}}>
                                <CreateIcon fontSize='large' style={{width: 72, height: 72, maxHeight: 72, color: theme.palette.primary.main}}/>
                            </IconButton>
                        </div>
                    </CardContent>
                    <CardActions>
                        <div 
                            style={{
                                display: 'flex', 
                                justifyContent: 'space-between', 
                                width: '100%', 
                                height: 48
                            }} 
                            onClick={e => handleCreate()}>
                            <Typography variant='h4' style={{marginLeft: 10}}>
                                Create new note
                            </Typography>
                        </div>
                        
                    </CardActions>
                    
                </Card>
                
            </div>

            <div style={{backgroundColor: theme.palette.secondary.main}} className={created ? 'transition-to-full-screen' : 'hide'}>
                <div style={{marginTop: '10vh'}}>
                    <div 
                        style={{
                            position: 'absolute', 
                            top: '10%', 
                            left: '5%', 
                            cursor: 'pointer',
                            visibility: created ? 'visible' : 'hidden'
                        }} 
                        onClick={() => {setCreated(false); setName(''); onCreate()}}
                    >
                        <Typography variant='h5' color='primary' style={{textDecoration: 'underline'}}>
                            cancel
                        </Typography>
                    </div>
                    {
                        created && (
                            <Paper 
                                style={{
                                    margin: '0 auto', 
                                    marginTop: '20vh', 
                                    width: '50%', 
                                    minHeight: '80vh', 
                                    paddingBottom: '10vh',
                                    borderRadius: '40px 40px 0 0', 
                                    boxShadow: '5px 5px 10px black',
                                    minWidth: 750
                                }} 
                            >
                                <Typography variant='h1' style={{paddingTop: '3vh', textAlign: 'center'}}>
                                    Name your note
                                </Typography>
                                <div style={{margin: '0 auto', marginTop: '5vh', display: 'flex', justifyContent: 'center', maxWidth: '80%'}}>
                                    <input 
                                        aria-label='name'
                                        type='text'
                                        value={name}
                                        onChange={e => setName(e.target.value)}
                                        style={{
                                            border: 'none', 
                                            fontSize: 48, 
                                            textAlign: 'center', 
                                            fontColor: 'black', 
                                            padding: 10,
                                            fontFamily: 'Roboto',
                                            maxWidth: '100%',
                                            fontWeight: 300
                                        }}
                                        placeholder='name'
                                        onKeyPress={(event) => handleEnter(event)}
                                        spellCheck='false'
                                        autoFocus={true}
                                    />
                                </div>
                                <Typography variant='h6' style={{color: theme.palette.error.main, textAlign: 'center', maxWidth: '60%', margin: '1vh auto'}}>
                                    {creationError}
                                </Typography>
                                <div style={{margin: '3vh auto', display: 'flex', justifyContent: 'center'}}>
                                    <PlainTooltip title={name !== '' ? '' : 'Please give your note a name'}>
                                        <div>
                                            <Button
                                                variant='contained'
                                                disabled={name === '' ? true : undefined}
                                                color='secondary'
                                                onClick={createNote}
                                            >
                                                Create note
                                            </Button>
                                        </div>
                                    </PlainTooltip>
                                </div>

                                
                            </Paper>
                        )
                    }
                </div>
            </div>
        </>
    )

}

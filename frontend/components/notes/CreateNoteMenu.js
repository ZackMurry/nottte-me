import React, { useState } from 'react'
import { Paper, IconButton } from '@material-ui/core'
import Cookie from 'js-cookie'
import { useRouter } from 'next/router'
import CloseIcon from '@material-ui/icons/Close'

//todo title validation
export default function CreateNoteMenu({ open, onClose }) {
    const [ title, setTitle ] = useState('')
    const jwt = Cookie.get('jwt')

    const router = useRouter()

    const createNote = async newTitle => {
        console.log(jwt)
        if (!jwt) return
        console.log('sending')

        //body.body is just a default editor state.
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt },
            body: JSON.stringify({
                newTitle
            })
        }

        const response = await fetch('http://localhost:8080/api/v1/notes/create', requestOptions)
        const { status } = response
        if (status === 200) {
            router.push('/n/' + encodeURI(newTitle)) //encoding so that it'll work in a URL
        }
        //else tell user
    }

    const handleKeyDown = e => {
        if (e.key === 'Enter') {
            console.log(title)
            e.preventDefault() //prevents enter key from being in title
            createNote(title)
        }
    }

    return (
        open
        && (
        <Paper elevation={5} style={{ width: '25vw', height: '35vh', borderRadius: 25 }}>
            <div style={{ paddingTop: 25 }}>
                <textarea
                    aria-label='title'
                    type='text'
                    value={title}
                    onChange={e => setTitle(e.target.value)}
                    style={{
                        border: 'none',
                        fontSize: 24,
                        textAlign: 'center',
                        fontColor: 'black',
                        marginLeft: '20%',
                        marginRight: '20%',
                        fontFamily: 'Roboto',
                        resize: 'none',
                        width: '60%'
                    }}
                    placeholder='title'
                    rows='5'
                    onKeyDown={handleKeyDown}
                />
            </div>
            <div style={{ position: 'absolute', top: '0.75vh', right: '0.75vw' }}>
                <IconButton onClick={onClose}>
                    <CloseIcon fontSize='large' />
                </IconButton>
            </div>
        </Paper>
        )

    )
}

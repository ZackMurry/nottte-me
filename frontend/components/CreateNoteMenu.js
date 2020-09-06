import React, { useState } from 'react'
import { Paper } from '@material-ui/core'
import Cookie from 'js-cookie'
import { useRouter, withRouter } from 'next/router'

//todo title validation
function CreateNoteMenu({ open }) {

    const [ title, setTitle ] = useState('')
    const jwt = Cookie.get('jwt')

    const router = useRouter()


    const handleKeyDown = (e) => {
        if(e.key === 'Enter') {
            console.log(title)
            e.preventDefault() //prevents enter key from being in title
            createNote(title)
        }
    }

    const createNote = async (title) => {
        console.log(jwt)
        if(!jwt) return
        console.log('sending')

        //body.body is just a default editor state. 
        const requestOptions = {
            method: 'POST',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt},
            body: JSON.stringify({
                title: title,
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
            router.push('/n/' + encodeURI(title)) //encoding so that it'll work in a URL
        } 
        //else tell user
    }

    return (
        open && 
        <Paper elevation={5} style={{width: '25vw', height: '35vh', borderRadius: 25}}>
            <div style={{paddingTop: 25}}>
                <textarea
                    aria-label='title'
                    type='text'
                    value={title}
                    onChange={e => setTitle(e.target.value)}
                    style={{border: 'none', fontSize: 24, textAlign: 'center', fontColor: 'black', marginLeft: '20%', marginRight: '20%', fontFamily: 'Roboto', resize: 'none', width: '60%'}}
                    placeholder='title'
                    rows='5'
                    onKeyDown={handleKeyDown}
                />
            </div>
        </Paper>
        
    )

}

export default withRouter(CreateNoteMenu)

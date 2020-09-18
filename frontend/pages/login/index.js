import React, { useState } from 'react'
import Cookie from 'js-cookie'
import Navbar from '../../components/Navbar'
import { Paper, Typography, Button } from '@material-ui/core'
import { useRouter, withRouter } from 'next/router'
import Link from 'next/link'

export default function Login() {
    
    const router = useRouter()

    const [ username, setUsername ] = useState('')
    const [ password, setPassword ] = useState('')

    const handleSubmit = async (e) => {
        e.preventDefault()
        
        const requestOptions = {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                username: username,
                password: password
            })
        }
        const response = await fetch('http://localhost:8080/api/v1/jwt/authenticate', requestOptions)
        const text = await response.text()
        console.log('jwt: ' + text)
        Cookie.set('jwt', JSON.parse(text).jwt)

        if(response.status == 200) {
            router.push('/notes')
        }

    }

    //after the username field, you can press enter to go to the password field
    const handleEnter = (event) => {
        if(event.key === 'Enter') {
            const form = event.target.form
            const index = Array.prototype.indexOf.call(form, event.target)
            form.elements[index+1].focus()
            event.preventDefault()
        }
    }

    return (
        <div>
            <div style={{marginTop: 0}} >
                <Navbar />
            </div>

            {/* main login */}
            <Paper style={{margin: '0 auto', marginTop: '20vh', width: '50%', height: '80vh', borderRadius: '40px 40px 0 0', boxShadow: '5px 5px 10px black'}} >
                
                <Typography variant='h1' style={{color: 'black', padding: '50px 0 25px 0', textAlign: 'center'}} >
                    Login
                </Typography>
                
                <form onSubmit={e => handleSubmit(e)} style={{textAlign: 'center'}}>
                    {/* username */}
                    <div>
                        <input 
                            aria-label='username'
                            type='text'
                            value={username}
                            onChange={e => setUsername(e.target.value)}
                            style={{border: 'none', fontSize: 24, textAlign: 'center', fontColor: 'black', padding: 10}}
                            placeholder='username'
                            onKeyPress={(event) => handleEnter(event)}
                        />
                    </div>
                    <div>
                        <input 
                            type='password'
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            style={{border: 'none', fontSize: 24, textAlign: 'center', fontColor: 'black', padding: 10}}
                            placeholder='password'
                        />
                    </div>
                    <div>
                        <Button type='submit' style={{backgroundColor: '#2d323e', color: '#fff', padding: '7.5px 15px', margin: 10}}>
                            Sign in
                        </Button>
                    </div>
                    <div style={{marginTop: 25}}>
                        <Typography style={{color: 'black'}}>
                            Don't have an account?
                            <Link href='/signup'>
                                <div style={{display: 'inline-flex', margin: '0 5px', cursor: 'pointer', textDecoration: 'underline'}}>
                                    Sign up.
                                </div>
                            </Link>
                        </Typography>
                    </div>
                </form>
            </Paper>
            
            
        </div>

        
    )

}

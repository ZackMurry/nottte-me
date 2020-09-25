import React, { useState } from 'react'
import Navbar from '../../components/Navbar'
import { Typography, Paper, Button } from '@material-ui/core'
import Cookie from 'js-cookie'
import { useRouter } from 'next/router'
import Link from 'next/link'

const USERNAME_LENGTH_REQUIRED_ERROR = "Your username must at least 4 characters"
const PASSWORD_LENGTH_REQUIRED_ERROR = "Your password must be at least 8 characters"

const USERNAME_TOO_LONG_ERROR = "Your username cannot be more than 32 characters"
const PASSWORD_TOO_LONG_ERROR = "Your password cannot be more than 40 characters"

const SPACE_IN_USERNAME_ERROR = "Your username cannot have a space in it"

//todo show errors as snackbars (username taken as error on form) and do some elementary password security
export default function SignUpPage() {

    const router = useRouter()

    const [ username, setUsername ] = useState('')
    const [ password, setPassword ] = useState('')

    const [ email, setEmail ] = useState('')

    const [ error, setError ] = useState('')

    const handleSubmit = async (e) => {
        e.preventDefault()
        
        if(!validateCredentials(username, password)) return

        const requestOptions = {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                username: username,
                password: password,
                email: email
            })
        }
        
        const createResponse = await fetch('http://localhost:8080/api/v1/users/create', requestOptions)
        
        if(createResponse.status !== 200) {
            if(createResponse.status == 411) {
                console.log('length required')
                return;
            } else if(createResponse.status == 400) {
                console.log('an account with that username already exists')
                return;
            }
            return;
        }
        console.log('account created!')
        

        const text = await (await fetch('http://localhost:8080/api/v1/jwt/authenticate', requestOptions)).text()
        console.log('jwt: ' + text)
        Cookie.set('jwt', JSON.parse(text).jwt)

        router.push('/notes')
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

    
    //used for making sure usernames and passwords are long enough
    const validateCredentials = (name, pass) => {
        name = name + ''
        pass = pass + ''

        if(name.length < 4) {
            setError(USERNAME_LENGTH_REQUIRED_ERROR)
            return false
        } else if(name.length > 32) {
            setError(USERNAME_TOO_LONG_ERROR)
            return false
        } else if(pass.length < 8) {
            setError(PASSWORD_LENGTH_REQUIRED_ERROR)
            return false
        } else if(pass.length > 40) {
            setError(PASSWORD_TOO_LONG_ERROR)
            return false
        } else if(name.includes(' ')) {
            setError(SPACE_IN_USERNAME_ERROR)
            return false
        }
        
        return true
    }
    
    const onEmailInvalid = e => {
        e.preventDefault()
        setError(e.target.validationMessage)
    }

    return (
        <div>
            <div style={{marginTop: 0}} >
                <Navbar />
            </div>

            {/* main login */}
            <Paper 
                style={{
                    margin: '0 auto', 
                    marginTop: '20vh', 
                    // todo just do margin: '20vh auto' in all of the papers like this
                    marginBottom: '20vh', 
                    width: '50%', 
                    minHeight: '120vh', 
                    paddingBottom: '10vh',
                    borderRadius: 40, 
                    boxShadow: '5px 5px 10px black',
                    minWidth: 750
                }} 
            >                
                <Typography variant='h1' style={{color: 'black', padding: '50px 0 25px 0', textAlign: 'center'}} >
                    Create account
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
                            aria-label='password'
                            type='password'
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            style={{border: 'none', fontSize: 24, textAlign: 'center', fontColor: 'black', padding: 10}}
                            placeholder='password'
                            onKeyPress={(event) => handleEnter(event)}
                        />
                    </div>
                    <div>
                        <input
                            aria-label='email'
                            type='email'
                            value={email}
                            onChange={e => setEmail(e.target.value)}
                            style={{border: 'none', fontSize: 24, textAlign: 'center', fontColor: 'black', padding: 10}}
                            placeholder='email (optional)'
                            required={false}
                            onInvalid={onEmailInvalid}
                        />
                    </div>
                    {/* errors */}
                    <Typography style={{color: 'red', fontWeight: 500}}>{error}</Typography>

                    <div>
                        <Button type='submit' style={{backgroundColor: '#2d323e', color: '#fff', padding: '7.5px 15px', margin: 10}}>
                            Sign up
                        </Button>
                    </div>
                </form>
                <div style={{display: 'inline-flex', marginTop: '2.5vh', width: '100%', justifySelf: 'center', justifyContent: 'center'}}>
                    <Typography style={{textAlign: 'center'}}>
                        Already have an account?
                    </Typography>
                    <Link href='/login'>
                        <div style={{margin: '0 5px', cursor: 'pointer', textDecoration: 'underline'}} className='MuiTypography-body1' >
                            Sign in.
                        </div>
                    </Link>
                </div>

            </Paper>
            
            
        </div>
        
    )

}

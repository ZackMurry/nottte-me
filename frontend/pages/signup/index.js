import React, { useState } from 'react'
import Navbar from '../../components/Navbar'
import { Typography, Paper, Button } from '@material-ui/core'
import Cookie from 'js-cookie'
import { useRouter, withRouter } from 'next/router'

//todo show errors as snackbars (username taken as error on form) and do some elementary password security
function SignUpPage() {

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
    
    return (
        <div>
            <div style={{marginTop: 0}} >
                <Navbar />
            </div>

            {/* main login */}
            <Paper style={{margin: '0 auto', marginTop: '20vh', width: '50%', height: '80vh', borderRadius: '40px 40px 0 0', boxShadow: '5px 5px 10px black'}} >
                
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
                            type='password'
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            style={{border: 'none', fontSize: 24, textAlign: 'center', fontColor: 'black', padding: 10}}
                            placeholder='password'
                        />
                    </div>
                    <div>
                        <Button type='submit' style={{backgroundColor: '#2d323e', color: '#fff', padding: '7.5px 15px', margin: 10}}>
                            Sign up
                        </Button>
                    </div>
                </form>
            </Paper>
            
            
        </div>
        
    )

}

export default withRouter(SignUpPage)
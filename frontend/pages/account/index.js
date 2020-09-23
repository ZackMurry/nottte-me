import React, { useState, useEffect } from 'react'
import Cookie from 'js-cookie'
import Navbar from '../../components/Navbar'
import { Link, Paper, Typography } from '@material-ui/core'
import EditEmail from '../../components/EditEmail'
import EmailViewEdit from '../../components/EditEmail'

//todo show statistics about notes?
export default function Account() {

    const initialJwt = Cookie.get('jwt')

    const [ jwt, setJwt ] = useState(initialJwt ? initialJwt : '')
    const [ username, setUsername ] = useState('')

    const [ user, setUser ] = useState({username: '', email: 'loading...', password: 'hidden'})

    useEffect(() => {
        async function getData() {
            if(jwt.length < 10) {
                console.log('unauthenticated')
                return;
            }
            setUsername(parseJwt(jwt).sub)

            const requestOptions = {
                headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt}
            }
            const response = await fetch('http://localhost:8080/api/v1/users/principal', requestOptions)

            if(response.status !== 200) {
                console.log(response.status)
                return
            }

            const text = await response.text()
            setUser(JSON.parse(text))

        }
        getData()
    }, [])


    const parseJwt = (token) => {
        var base64Url = token.split('.')[1]
        var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
        var jsonPayload = decodeURIComponent(atob(base64).split('').map(
            (c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
        ).join(''))
    
        return JSON.parse(jsonPayload);
    }

    return (
        <div>
            <Navbar />
            <div style={{marginTop: '15vh'}}></div>
            <Paper 
                style={{
                    margin: '0 auto', 
                    marginTop: '20vh', 
                    marginBottom: '20vh', 
                    width: '50%', 
                    minHeight: '120vh', 
                    paddingBottom: '10vh',
                    borderRadius: 40, 
                    boxShadow: '5px 5px 10px black',
                    minWidth: 750
                }} 
            >
                <Typography variant='h1' style={{textAlign: 'center', padding: '50px 0 25px 0'}}>
                    Account
                </Typography>
                
                {/* main */}
                <div>
                    <Paper elevation={0} style={{width: '75%', padding: 10, margin: '0 auto'}}>
                        <Typography>
                            Username: {username}
                        </Typography>
                        <EditEmail
                            currentEmail={user.email}
                            jwt={jwt}
                        />
                        <Typography style={{marginTop: 5}}>
                            <Link href='/help/passwords'>
                                <span style={{color: 'black', textDecoration: 'underline'}}>Password security</span>
                            </Link>
                        </Typography>
                    </Paper>

                </div>

            </Paper>
        </div>
        
    )

}

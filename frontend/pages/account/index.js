import React, { useState, useEffect } from 'react'
import Cookie from 'js-cookie'
import { Link, Paper, Typography } from '@material-ui/core'
import Navbar from '../../components/Navbar'
import EditEmail from '../../components/account/EditEmail'
import parseJwt from '../../components/utils/ParseJwt'
import EditPassword from '../../components/account/EditPassword'

//todo show statistics about notes?
export default function Account() {
    const initialJwt = Cookie.get('jwt')

    const [ jwt ] = useState(initialJwt || '')
    const [ username, setUsername ] = useState('')

    const [ user, setUser ] = useState({ username: '', email: 'loading...', password: 'hidden' })
    const [ editedEmail, setEditedEmail ] = useState(user.email)

    useEffect(() => {
        async function getData() {
            if (!jwt) {
                console.log('unauthenticated')
                return
            }
            setUsername(parseJwt(jwt).sub)

            const requestOptions = {
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}` }
            }
            const response = await fetch('http://localhost:8080/api/v1/users/principal', requestOptions)

            if (response.status !== 200) {
                console.log(response.status)
                return
            }

            const text = await response.text()
            setUser(JSON.parse(text))
        }
        getData()
    }, [])

    return (
        <div>
            <Navbar />
            <div style={{ marginTop: '15vh' }} />
            <Paper
                style={{
                    margin: '20vh auto',
                    width: '50%',
                    minHeight: '120vh',
                    paddingBottom: '10vh',
                    borderRadius: 40,
                    boxShadow: '5px 5px 10px black',
                    minWidth: 750
                }}
            >
                <Typography variant='h1' style={{ textAlign: 'center', padding: '50px 0 25px 0' }}>
                    Account
                </Typography>
                {/* main */}
                <div>
                    <div style={{ width: '75%', padding: 10, margin: '0 auto' }}>
                        <Typography>
                            Username:
                            {' ' + username}
                        </Typography>
                        <EditEmail
                            currentEmail={user.email}
                            jwt={jwt}
                        />
                        <Typography style={{ marginTop: 5 }}>
                            <Link href='/help/passwords'>
                                <span style={{ color: 'black', textDecoration: 'underline' }}>
                                    Password security
                                </span>
                            </Link>
                        </Typography>

                        <EditPassword jwt={jwt} style={{ marginTop: '3vh' }} />
                    </div>
                </div>

            </Paper>
        </div>
    )
}

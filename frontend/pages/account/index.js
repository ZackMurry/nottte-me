import React, { useState, useEffect } from 'react'
import Cookie from 'js-cookie'
import Navbar from '../../components/Navbar'
import { Paper, Typography } from '@material-ui/core'

//todo show statistics about notes?
export default function Account() {

    const initialJwt = Cookie.get('jwt')

    const [ jwt, setJwt ] = useState(initialJwt ? initialJwt : '')
    const [ username, setUsername ] = useState('')

    useEffect(() => {
        async function getData() {
            if(jwt.length < 10) {
                console.log('unauthenticated')
                return;
            }
            setUsername(parseJwt(jwt).sub)
        }
        getData()
    }, [])


    function parseJwt (token) {
        var base64Url = token.split('.')[1];
        var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
    
        return JSON.parse(jsonPayload);
    };

    return (
        <div>
            <Navbar />
            <div style={{marginTop: '15vh'}}></div>
            <Paper style={{margin: '0 auto', marginTop: '20vh', width: '50%', height: '80vh', borderRadius: '40px 40px 0 0', boxShadow: '5px 5px 10px black'}} >
                <Typography variant='h1' style={{textAlign: 'center', padding: '50px 0 25px 0'}}>
                    Account
                </Typography>
                
                {/* main */}
                <div>
                    <Typography variant='h4' style={{textAlign: 'center'}}>
                        Signed in as { username }
                    </Typography>
                </div>

            </Paper>
        </div>
        
    )

}

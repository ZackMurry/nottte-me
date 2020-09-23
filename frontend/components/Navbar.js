import React, { useState, useEffect } from 'react'
import { Typography, IconButton } from '@material-ui/core'
import Link from 'next/link'
import Cookie from 'js-cookie'

//todo have account if user is logged in
export default function Navbar() {

    const [ jwt, setJwt ] = useState('')

    //used because this needs to be rendered on client
    useEffect(() => {
        setJwt(Cookie.get('jwt'))
    }, [])

    return (
        <div style={{backgroundColor: '#2d323e', width: '100%', display: 'flex', height: '10vh', position: 'fixed', zIndex: 1, top: 0, left: 0}}>
            <div>
                <Link href="/">
                    <Typography color='primary' style={{fontWeight: 100, paddingLeft: '7.5vw', paddingTop: 10, fontSize: 48, alignSelf: 'center', cursor: 'pointer'}}>
                        nottte.me
                    </Typography>
                </Link>
            </div>
            <div style={{display: 'flex', width: '80%', alignSelf: 'flex-start', justifySelf: 'flex-end', justifyContent: 'flex-end', height: '10vh', alignItems: 'center', paddingTop: 10}}>
                <Link href="/notes">
                    <Typography color='primary' style={{fontSize: 36, fontWeight: 100, marginRight: '3vw', cursor: 'pointer'}}>
                        notes
                    </Typography>
                </Link>
                <Link href="/about">
                    <Typography color='primary' style={{fontSize: 36, fontWeight: 100, marginRight: '3vw', cursor: 'pointer'}}>
                        about
                    </Typography>
                </Link>
                {
                    !jwt && (
                        <Link href="/login">
                            <Typography color='primary' style={{marginRight: '7.5vw', fontSize: 36, fontWeight: 100, cursor: 'pointer'}}>
                                login
                            </Typography>
                        </Link>
                    )
                }
                
                {
                    jwt && (
                        <Link href="/account">
                            <Typography color='primary' style={{marginRight: '7.5vw', fontSize: 36, fontWeight: 100, cursor: 'pointer'}}>
                                account
                            </Typography>
                        </Link>
                    )
                }

            </div>
        </div>
    )

}

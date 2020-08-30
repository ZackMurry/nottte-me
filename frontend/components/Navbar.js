import React from 'react'
import { Typography } from '@material-ui/core'
import Link from 'next/link'

export default function Navbar() {

    return (
        <div style={{backgroundColor: '#2d323e', width: '100%', display: 'flex', position: 'fixed', top: 0, left: 0}}>
            <div>
                <Link href="/">
                    <Typography color='primary' style={{fontWeight: 100, paddingLeft: '7.5vw', paddingTop: 10, fontSize: 48, alignSelf: 'flex-end', cursor: 'pointer'}}>
                        nottte.me
                    </Typography>
                </Link>
            </div>
            <div style={{display: 'flex', width: '80%', alignSelf: 'flex-end', justifyContent: 'flex-end'}}>
                {/* todo probably replace home with something better*/}
                <Link href="/">
                    <Typography color='primary' style={{fontSize: 36, fontWeight: 100, marginRight: '3vw', cursor: 'pointer'}}>
                        home
                    </Typography>
                </Link>
                <Link href="/about">
                    <Typography color='primary' style={{fontSize: 36, fontWeight: 100, marginRight: '3vw', cursor: 'pointer'}}>
                        about
                    </Typography>
                </Link>
                <Link href="/login">
                    <Typography color='primary' style={{marginRight: '7.5vw', fontSize: 36, fontWeight: 100, cursor: 'pointer'}}>
                        login
                    </Typography>
                </Link>
            </div>
        </div>
    )

}


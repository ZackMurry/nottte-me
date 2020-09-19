import { Paper, Typography } from '@material-ui/core'
import Head from 'next/head'
import Link from 'next/link'
import { useRouter } from 'next/router'
import React, { useEffect } from 'react'
import Navbar from '../../../../components/Navbar'
import Cookie from 'js-cookie'
import RenameNoteField from '../../../../components/RenameNoteField'

//todo this is showing a warning of "Unknown key passed via urlObject into url.format: searchParams"
export default function Settings() {

    const router = useRouter()
    const { title } = router.query

    const jwt = Cookie.get('jwt')

    useEffect(() => {
        if(jwt) {
            if(!userHasNote()) {
                router.push('/notes')
            }
        } else if(title){
            //if user is unauthenticated and title has been found
            router.push({
                pathname: '/login',
                query: {redirect: `/n/${encodeURI(title)}/settings`}
            })
        } else {
            //if title hasn't been found, just send to /login
            router.push('/login')
        }

    }, [ title ])

    const userHasNote = async () => {
        const requestOptions = {
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt}
        }

        const response = await fetch(`http://localhost:8080/api/v1/notes/principal/has/${encodeURI(title)}`, requestOptions)
        return await response.text()
    }

    return (
        <div>
            <Head>
                <title>{title} settings | nottte.me</title>
            </Head>
            <div style={{marginTop: 0}} >
                <Navbar />
            </div>

            {/* main login */}
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
                <Typography variant='h1' style={{textAlign: 'center', padding: '2vh 0'}}>
                    Note settings
                </Typography>
                <div id='table-of-contents' style={{marginTop: '5vh'}}>
                    <Typography variant='h4' style={{textAlign: 'center'}}>
                        <Link href={`/n/${encodeURI(title)}/settings`}>
                            General
                        </Link>
                    </Typography>
                    <Typography variant='h4' style={{textAlign: 'center'}}>
                        <Link href='/shortcuts'>
                            Shortcuts
                        </Link>
                    </Typography>
                    <Typography variant='h4' style={{textAlign: 'center'}}>
                        <Link href={`/n/${encodeURI(title)}/settings/export`}>
                            Export
                        </Link>
                    </Typography>
                </div>
                
                
                <div style={{height: '55vh'}}></div>

                <div id='general'>
                    <div id='rename'>
                        <Typography variant='h3' style={{textAlign: 'center'}}>
                            Rename note
                        </Typography>
                        <div style={{width: '60%', margin: '5vh auto', textAlign: 'center'}}>
                            <Typography variant='h5' style={{fontWeight: 300}}>
                                Current note name: {title}
                            </Typography>
                            <RenameNoteField name={router.query.title} jwt={jwt} />
                        </div>
                    </div>
                </div>

            </Paper>

        </div>
        
    )

}

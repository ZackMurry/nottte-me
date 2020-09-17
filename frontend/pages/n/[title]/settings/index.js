import { Paper, Typography } from '@material-ui/core'
import Head from 'next/head'
import Link from 'next/link'
import { useRouter, withRouter } from 'next/router'
import React from 'react'
import Navbar from '../../../../components/Navbar'

function Settings() {

    const router = useRouter()
    const { title } = router.query

    return (
        <div>
            <Head>
                <title>{title} settings | nottte.me</title>
            </Head>
            <div style={{marginTop: 0}} >
                <Navbar />
            </div>

            {/* main login */}
            <Paper style={{margin: '20vh auto 15vh auto', padding: '5vh 0 25vh 0', marginTop: '20vh', width: '50%', borderRadius: 40, boxShadow: '5px 5px 10px black', minWidth: 500}} >
                <Typography variant='h1' style={{textAlign: 'center', padding: '2vh 0'}}>
                    Note settings
                </Typography>
                <Typography variant='h4' style={{textAlign: 'center'}}>
                    <Link href={`/shortcuts`}>
                        Shortcuts
                    </Link>
                </Typography>
            </Paper>

        </div>
        
    )

}

export default withRouter(Settings)

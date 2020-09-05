import React, { useState, useEffect } from 'react'
import Navbar from '../../components/Navbar'
import { Paper, Typography, Grid } from '@material-ui/core'
import Head from 'next/head'
import TextShortcutEditor from '../../components/TextShortcutEditor'
import CreateTextShortcut from '../../components/CreateTextShortcut'
import Cookie from 'js-cookie'
import { useRouter, withRouter } from 'next/router'
import NottteShortcutDisplay from '../../components/NottteShortcutDisplay'

//default text shortcuts
const nottteShortcuts = [
    {
        name: 'nottte-save',
        key: 's',
        description: "saves the current note (for when auto-saving isn't enough)"
    }
]

//todo style shortcuts
function Shortcuts() {

    const router = useRouter()

    const [ jwt, setJwt ] = useState(Cookie.get('jwt'))
    const [ textShortcuts, setTextShortcuts ] = useState([])

    useEffect(() => {
        if(jwt) {
            getShortcuts()
        } else {
            //todo redirect back here once logged in
            router.push('/login')
        }
    }, [])

    const getShortcuts = async () => {

        //getting text shortcuts
        const requestOptions = {
            method: 'GET',
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt}
        }

        const response = await fetch('http://localhost:8080/api/v1/users/principal/preferences/shortcuts/text', requestOptions)
        console.log(response.status)
        const text = await response.text()
        await setTextShortcuts(JSON.parse(text))
        console.log(textShortcuts)
    }


    return (
        <div>
            <Head>
                <title>Shortcuts | nottte.me</title>
            </Head>
            <div>
                <Navbar />
            </div>
            
            <Paper 
                style={{
                    margin: '0 auto', 
                    marginTop: '20vh', 
                    marginBottom: '20vh', 
                    width: '50%', 
                    minHeight: '120vh', 
                    paddingBottom: '10vh',
                    borderRadius: 40, 
                    boxShadow: '5px 5px 10px black'
                }} 
            >
                <Typography variant='h2' style={{textAlign: 'center', paddingTop: '2.5vh', marginBottom: '2.5vh'}}>
                    Shortcuts
                </Typography>
                <div style={{margin: '5% 10%'}}>

                    {/* text shortcuts */}
                    <Typography variant='h4' style={{textAlign: 'center', marginBottom: '3.5vh', fontWeight: 300}}>
                        Text shortcuts
                    </Typography>
                    <Typography 
                        variant='h6' 
                        style={{
                            padding: 0, 
                            fontWeight: 100, 
                            textAlign: 'center', 
                            width: '80%', 
                            marginLeft: 'auto', 
                            marginRight: 'auto', 
                            marginBottom: '3vh'}}
                    >
                        Text shortcuts insert text at your cursor when you press the combination of keys that activate them
                    </Typography>

                    {/* if user doesn't have any text shortcuts, show this */}
                    {
                        textShortcuts.length == 0 && (
                            <Typography variant='h6' style={{textAlign: 'center', fontWeight: 300, marginBottom: '3vh'}}>
                                You don't have any shortcuts. You should make some!
                            </Typography>
                        )
                    }

                    <Grid container spacing={3} style={{marginBottom: '3vh'}}>
                        {
                            textShortcuts.length !== 0 && (
                                <Grid item xs={12}>
                                    {/* labels of columns at the top — just a custom TextShortcutEditor */}
                                    <Grid container spacing={3}>
                                        <Grid item xs={4}>
                                            <Typography variant='h6' style={{fontWeight: 700}} >name</Typography>
                                        </Grid>
                                        <Grid item xs={2}>
                                            <Typography variant='h6' style={{fontWeight: 700}} >shortcut</Typography>
                                        </Grid>
                                        <Grid item xs={6}>
                                            <Typography variant='h6' style={{fontWeight: 700}} >text</Typography>
                                        </Grid>
                                    </Grid>
                                </Grid>
                            )
                        }
                        
                        {
                            textShortcuts && textShortcuts.map(textShortcut => {
                                return (
                                    <Grid item xs={12} key={textShortcut.name}>
                                        <TextShortcutEditor name={textShortcut.name} button={textShortcut.key} text={textShortcut.text} key={textShortcut.name}/>
                                    </Grid>
                                )
                            })
                        }

                        <Grid item xs={12}>
                            <Typography variant='h6' style={{textAlign: 'center'}}>
                                Create custom text shortcut
                            </Typography>
                        </Grid>
                        <Grid item xs={12}>
                            <CreateTextShortcut jwt={jwt}/>
                        </Grid>
                    </Grid>
                    
                    {/* default shortcuts */}
                    <Typography variant='h4' style={{textAlign: 'center', marginBottom: '3.5vh', marginTop: '3.5vh', fontWeight: 300}}>
                        Reserved shortcuts
                    </Typography>
                    <Typography 
                        variant='h6' 
                        style={{
                            padding: 0, 
                            fontWeight: 100, 
                            textAlign: 'center', 
                            width: '80%', 
                            marginLeft: 'auto', 
                            marginRight: 'auto', 
                            marginBottom: '3.5vh'
                        }}
                    >
                        These are shortcuts that are automatically added with nottte.me.
                        They pertain to complex functions that don't directly affect the editor,
                        so you can't delete or edit them.
                    </Typography>
                    <Grid container spacing={3}>
                        {/* column labels */}
                        <Grid item xs={12}>
                            <Grid container spacing={3}>
                                <Grid item xs={4}>
                                    <Typography variant='h6' style={{fontWeight: 700}}>name</Typography>
                                </Grid>
                                <Grid item xs={2}>
                                    <Typography variant='h6' style={{fontWeight: 700}}>button</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant='h6' style={{fontWeight: 700}}>description</Typography>
                                </Grid>
                            </Grid>
                        </Grid>

                        {/* values */}
                        {
                            nottteShortcuts.map(nottteShortcut => {
                                return (
                                    <Grid item xs={12} key={nottteShortcut.name}>
                                        <NottteShortcutDisplay name={nottteShortcut.name} button={nottteShortcut.key} description={nottteShortcut.description} />
                                    </Grid>
                                )
                            })
                        }
                        
                    </Grid>
                </div>
            </Paper>

        </div>
        
    )

}

export default withRouter(Shortcuts)
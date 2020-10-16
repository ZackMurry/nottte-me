import {
    ClickAwayListener, Collapse, Link, Paper, Typography
} from '@material-ui/core'
import { useEffect, useState } from 'react'
import theme from '../theme'

export default function SmallScreenNavbar({ jwt }) {
    const [ menuOpen, setMenuOpen ] = useState(false)
    const [ prevScrollPos, setPrevScrollPos ] = useState(0)
    const [ showing, setShowing ] = useState(true)

    const handleMenuClick = () => {
        setMenuOpen(!menuOpen)
    }

    const handleScroll = () => {
        const currentScrollPos = window.pageYOffset
        if (prevScrollPos < currentScrollPos) {
            console.log('down: ' + currentScrollPos + ' - ' + prevScrollPos)
            if (showing) {
                setShowing(false)
            }
        } else if (!showing) {
            setShowing(true)
        }
        setPrevScrollPos(currentScrollPos)
        console.log(showing + '; ' + currentScrollPos)
    }

    useEffect(() => {
        window.addEventListener('scroll', handleScroll)
        return () => window.removeEventListener('scroll', handleScroll)
    }, [ showing, prevScrollPos, setPrevScrollPos ])

    return (
        <ClickAwayListener onClickAway={() => setMenuOpen(false)}>
            <div
                style={{
                    width: '100%',
                    position: 'fixed',
                    top: 0,
                    left: 0,
                    zIndex: 10,
                    backgroundColor: theme.palette.secondary.main,
                    height: '10vh',
                    justifyContent: 'space-between'
                }}
                className={showing ? 'show-nav' : 'hide-nav'}
            >
                <div
                    style={{
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                        width: 80,
                        height: 80,
                        cursor: 'pointer',
                        transition: 'all .5s ease-in-out'
                    }}
                    onClick={handleMenuClick}
                    className={menuOpen ? 'navbar-menu-open' : undefined}
                >
                    <div className='navbar-burger' />
                </div>
                <Collapse in={menuOpen} timeout={750} style={{ boxShadow: '0 15px 25px -12.5px black' }}>
                    <Paper elevation={0} style={{ borderRadius: 0 }}>
                        <Paper
                            elevation={0}
                            style={{
                                backgroundColor: theme.palette.secondary.main,
                                height: '5vh',
                                borderRadius: 0,
                                padding: 15,
                                cursor: 'pointer'
                            }}
                        >
                            <Link href='/'>
                                <Typography variant='h5' color='primary'>
                                    home
                                </Typography>
                            </Link>
                        </Paper>
                        <Paper
                            elevation={0}
                            style={{
                                backgroundColor: theme.palette.secondary.main,
                                height: '5vh',
                                borderRadius: 0,
                                padding: 15,
                                cursor: 'pointer'
                            }}
                        >
                            <Link href='/notes'>
                                <Typography variant='h5' color='primary'>
                                    notes
                                </Typography>
                            </Link>
                        </Paper>
                        <Paper
                            elevation={0}
                            style={{
                                backgroundColor: theme.palette.secondary.main, height: '5vh', borderRadius: 0, padding: 15, cursor: 'pointer'
                            }}
                        >
                            <Link href='/shortcuts'>
                                <Typography variant='h5' color='primary'>
                                    shortcuts
                                </Typography>
                            </Link>
                        </Paper>
                        <Paper
                            elevation={0}
                            style={{
                                backgroundColor: theme.palette.secondary.main, height: '7vh', borderRadius: 0, padding: 15, cursor: 'pointer'
                            }}
                        >
                            <Link href={jwt ? '/account' : '/login'}>
                                <Typography variant='h5' color='primary'>
                                    {jwt ? 'account' : 'login'}
                                </Typography>
                            </Link>
                        </Paper>
                    </Paper>

                </Collapse>
                <Link href='/'>
                    <Typography
                        color='primary'
                        style={{
                            position: 'fixed', top: '1.5vh', right: 30, fontWeight: 100, fontSize: 36, cursor: 'pointer'
                        }}
                    >
                        nottte.me
                    </Typography>
                </Link>

            </div>
        </ClickAwayListener>

    )
}

import {useAuth} from "../context/AuthContext.jsx";
import {useNavigate} from "react-router-dom";
import {useEffect} from "react";
import {Flex, Heading, Image, Link, Stack, Text} from "@chakra-ui/react";
import CreateCustomerForm from "../shared/CreateCustomerForm.jsx";

const Signup = () => {

    const {customer, setCustomerFromToken} = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (customer) {
            navigate("/dashboard")
        }
    })

    return (
        <Stack minH={'100vh'} direction={{ base: 'column', md: 'row' }}>
            <Flex p={8} flex={1} alignItems={'center'} justifyContent={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Heading fontSize={'2xl'} mb={15}>Create can account</Heading>
                    <CreateCustomerForm onSuccess={(token) => {
                        localStorage.setItem("access_token", token)
                        setCustomerFromToken()
                        navigate("/dashboard/customers")
                    }}/>
                    <Link color={"blue.500"} href={"/"}>
                        Already have an account? Login now.
                    </Link>
                </Stack>
            </Flex>
            <Flex
                flex={1}
                p={10}
                flexDirection={"column"}
                alignItems={"center"}
                justifyContent={"center"}
                bgGradient={{sm: 'linear(to-r, blue.600, green.600)'}}
            >
                <Text fontSize={"5xl"} color={'white'} fontWeight={"bold"} mb={5}>
                    <Link href={"https://babbybubble.com"}>
                        Enroll Now
                    </Link>
                </Text>
                <Image
                    alt={'Login Image'}
                    objectFit={'scale-down'}
                    src={
                        'https://i.ibb.co/191CTpS/Screenshot-2023-09-28-at-6-front.png'
                    }
                />
            </Flex>
        </Stack>
    )
}

export default Signup;
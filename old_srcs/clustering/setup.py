from setuptools import setup, find_packages
import cluster

setup(
    name="clustering",
    version=cluster.__version__,
    packages=find_packages(),

    install_requires=[
        'numpy==1.11.1, >=1.11.1',
        'scikit-learn==0.17.1, >=0.17.1',
        'matplotlib==1.5.1, >=1.5.1'
    ],

    test_suite='tests',
)



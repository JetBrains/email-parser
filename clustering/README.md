Python 3.5 is used for this project.

#### Build
Enter `python setup.py install` to build project and download dependencies.

#### Run
Setup `PYTHONIOENCODING=utf-8` environment variable.

To run edit distance example: `python executable\edit_distance_executable.py` .

To run affinity_propagation clustering:
`python executable\affinity_propagation.py <input_dataset_filename> <output_filename> <distance_matrix_filename(optional)>` .

Data format for input/output files:

    <email_number> <header>

Clusters are separated with a blank line.

#### Test
Enter `python setup.py test` to test project.